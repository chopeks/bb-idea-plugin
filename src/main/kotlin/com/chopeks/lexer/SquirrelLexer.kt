package com.chopeks.lexer

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_BODY
import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_END
import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_START
import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT
import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT_START
import com.chopeks.SquirrelTokenTypesSets.WHITE_SPACES
import com.chopeks._SquirrelLexer
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergeFunction
import com.intellij.lexer.MergingLexerAdapterBase

class SquirrelLexer : MergingLexerAdapterBase(createLexer()) {
	override fun getMergeFunction(): MergeFunction {
		return MERGE_FUNCTION
	}

	companion object {
		private fun createLexer(): FlexAdapter {
			return FlexAdapter(object : _SquirrelLexer() {
				override fun reset(buffer: CharSequence, start: Int, end: Int, initialState: Int) {
					super.reset(buffer, start, end, initialState)
					//                myLeftParenCount = 0;
//                myStateStack.clear();
				}
			})
		}

		/**
		 * Collapses sequence like `{MULTI_LINE_(DOC_)COMMENT_START MULTI_LINE_COMMENT_BODY* MULTI_LINE_COMMENT_END}` into a single `SquirrelTokenTypesSets.MULTI_LINE_(DOC_)COMMENT`.
		 * Doc comment content is lazily parsed separately
		 */
		private val MERGE_FUNCTION = MergeFunction { firstTokenType, originalLexer ->
			if (WHITE_SPACES.contains(firstTokenType)) {
				while (true) {
					if (!WHITE_SPACES.contains(originalLexer.tokenType)) break
					originalLexer.advance()
				}
				return@MergeFunction firstTokenType
			} else if (firstTokenType === MULTI_LINE_COMMENT_START || firstTokenType === MULTI_LINE_DOC_COMMENT_START) {
				// merge multiline comments that are parsed in parts into single element
				while (true) {
					val nextTokenType = originalLexer.tokenType ?: break
					// EOF reached, multi-line comment is not closed


					originalLexer.advance()
					if (nextTokenType === MULTI_LINE_COMMENT_END) break

					assert(nextTokenType === MULTI_LINE_COMMENT_BODY) { nextTokenType }
				}

				return@MergeFunction if (firstTokenType === MULTI_LINE_DOC_COMMENT_START)
					MULTI_LINE_DOC_COMMENT
				else
					SquirrelTokenTypes.MULTI_LINE_COMMENT
			}
			firstTokenType
		}
	}
}