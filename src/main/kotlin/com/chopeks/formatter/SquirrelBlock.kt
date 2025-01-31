package com.chopeks.formatter

import com.chopeks.SquirrelFileType
import com.chopeks.SquirrelLanguage
import com.chopeks.SquirrelTokenTypes
import com.chopeks.formatter.SquirrelIndentProcessor.getChildIndent
import com.chopeks.formatter.SquirrelWrappingProcessor.createChildWrap
import com.chopeks.formatter.settings.SquirrelCodeStyleSettings
import com.intellij.formatting.*
import com.intellij.formatting.templateLanguages.BlockWithParent
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet

class SquirrelBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, settings: CodeStyleSettings) : AbstractBlock(node, wrap, alignment), BlockWithParent {
	private val myNode: ASTNode = node
	private val mySettings: CodeStyleSettings = settings
	private val cmSettings: CommonCodeStyleSettings = settings.getCommonSettings(SquirrelLanguage)
	private val sqSettings: SquirrelCodeStyleSettings = settings.getCustomSettings(SquirrelCodeStyleSettings::class.java)

	private var myParent: BlockWithParent? = null
	private var mySubSquirrelBlocks: MutableList<SquirrelBlock>? = null

	override fun getIndent(): Indent? {
		return getChildIndent(myNode, cmSettings, sqSettings)
	}

	override fun getSpacing(child1: Block?, child2: Block): Spacing? {
		return SquirrelSpacingProcessor(child1, child2, myNode, cmSettings, sqSettings).spacing
	}

	override fun buildChildren(): List<Block> {
		if (isLeaf) {
			return EMPTY
		}
		val tlChildren = ArrayList<Block>()
		var childNode = node.firstChildNode
		while (childNode != null) {
			if (FormatterUtil.containsWhiteSpacesOnly(childNode)) {
				childNode = childNode.treeNext
				continue
			}
			val childBlock = SquirrelBlock(
				childNode, createChildWrap(childNode),
				createChildAlignment(childNode), mySettings
			)
			childBlock.parent = this
			tlChildren.add(childBlock)
			childNode = childNode.treeNext
		}
		return tlChildren
	}

	fun createChildWrap(child: ASTNode): Wrap {
		return createChildWrap(child, Wrap.createWrap(WrapType.NONE, false), myNode, cmSettings, sqSettings)
	}

	protected fun createChildAlignment(child: ASTNode?): Alignment? {
		return SquirrelAlignmentProcessor.createChildAlignment(child, myNode, cmSettings, sqSettings)
	}

	override fun isLeaf(): Boolean {
		return false
	}

	override fun getParent(): BlockWithParent {
		return myParent!!
	}

	override fun setParent(newParent: BlockWithParent) {
		myParent = newParent
	}

	override fun getChildAttributes(newIndex: Int): ChildAttributes {
		val elementType = myNode.elementType
		val previousBlock = if (newIndex == 0) null else subSquirrelBlocks[newIndex - 1]
		val previousType = previousBlock?.node?.elementType

		if (previousType === SquirrelTokenTypes.LBRACE || previousType === SquirrelTokenTypes.LBRACKET) {
			return ChildAttributes(Indent.getNormalIndent(), null)
		}

		if (previousType === SquirrelTokenTypes.RPAREN && STATEMENTS_WITH_OPTIONAL_BRACES.contains(elementType) ||
			previousType === SquirrelTokenTypes.PARAMETERS && elementType === SquirrelTokenTypes.FUNCTION_DECLARATION
		) {
			return ChildAttributes(Indent.getNormalIndent(), null)
		}

		if (previousType === SquirrelTokenTypes.COLON && (elementType === SquirrelTokenTypes.SWITCH_CASE || elementType === SquirrelTokenTypes.DEFAULT_CASE)) {
			return ChildAttributes(Indent.getNormalIndent(), null)
		}

		if (previousType === SquirrelTokenTypes.SWITCH_CASE || previousType === SquirrelTokenTypes.DEFAULT_CASE) {
			if (previousBlock != null) {
				val subBlocks = previousBlock.subSquirrelBlocks
				if (!subBlocks.isEmpty()) {
					val lastChildInPrevBlock = subBlocks[subBlocks.size - 1]
					val subSubBlocks = lastChildInPrevBlock.subSquirrelBlocks
					if (isLastTokenInSwitchCase(subSubBlocks)) {
						return ChildAttributes(Indent.getNormalIndent(), null) // e.g. Enter after BREAK_STATEMENT
					}
				}
			}

			val indentSize = mySettings.getIndentSize(SquirrelFileType.INSTANCE) * 2
			return ChildAttributes(Indent.getIndent(Indent.Type.SPACES, indentSize, false, false), null)
		}

		if (previousBlock == null) {
			return ChildAttributes(Indent.getNoneIndent(), null)
		}

		if (!previousBlock.isIncomplete && newIndex < subSquirrelBlocks.size && previousType !== TokenType.ERROR_ELEMENT) {
			return ChildAttributes(previousBlock.indent, previousBlock.alignment)
		}
		if (myParent is SquirrelBlock && (myParent as SquirrelBlock).isIncomplete) {
			val child = myNode.firstChildNode ?: return ChildAttributes(Indent.getContinuationIndent(), null)
		}
		if (myParent == null && isIncomplete) {
			return ChildAttributes(Indent.getContinuationIndent(), null)
		}
		return ChildAttributes(previousBlock.indent, previousBlock.alignment)
	}

	val subSquirrelBlocks: List<SquirrelBlock>
		get() {
			if (mySubSquirrelBlocks == null) {
				mySubSquirrelBlocks = ArrayList()
				for (block in subBlocks) {
					mySubSquirrelBlocks!!.add(block as SquirrelBlock)
				}
				mySubSquirrelBlocks = if (!mySubSquirrelBlocks!!.isEmpty()) mySubSquirrelBlocks else Squirrel_EMPTY
			}
			return mySubSquirrelBlocks!!
		}

	companion object {
		val Squirrel_EMPTY: MutableList<SquirrelBlock> = mutableListOf()

		private val STATEMENTS_WITH_OPTIONAL_BRACES = TokenSet.create(
			SquirrelTokenTypes.FOR_STATEMENT,
			SquirrelTokenTypes.FOREACH_STATEMENT,
			SquirrelTokenTypes.WHILE_STATEMENT,
			SquirrelTokenTypes.DO_WHILE_STATEMENT,
			SquirrelTokenTypes.IF_STATEMENT,
			SquirrelTokenTypes.TRY_STATEMENT,
			SquirrelTokenTypes.FUNCTION_DECLARATION
		)

		private val LAST_TOKENS_IN_SWITCH_CASE = TokenSet.create(
			SquirrelTokenTypes.BREAK_STATEMENT, SquirrelTokenTypes.CONTINUE_STATEMENT,
			SquirrelTokenTypes.RETURN_STATEMENT
		)


		private fun isLastTokenInSwitchCase(blocks: List<SquirrelBlock>): Boolean {
			val size = blocks.size
			// No blocks.
			if (size == 0) {
				return false
			}
			// [return x;]
			val lastBlock = blocks[size - 1]
			val type = lastBlock.node.elementType
			if (LAST_TOKENS_IN_SWITCH_CASE.contains(type)) {
				return true
			}
			// [throw expr][;]
			if (type === SquirrelTokenTypes.SEMICOLON && size > 1) {
				val lastBlock2 = blocks[size - 2]
				return lastBlock2.node.elementType === SquirrelTokenTypes.THROW_STATEMENT
			}
			return false
		}
	}
}