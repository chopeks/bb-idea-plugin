package com.chopeks.highlighting

import com.chopeks.SquirrelIcons
import com.chopeks.SquirrelLanguage
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage

class SquirrelColorSettingsPage : ColorSettingsPage {
	override fun getDisplayName() = SquirrelLanguage.displayName
	override fun getIcon() = SquirrelIcons.SQUIRREL
	override fun getHighlighter() = SquirrelSyntaxHighlighter()
	override fun getAttributeDescriptors() = arrayOf(
		AttributesDescriptor("Single line comment", SquirrelSyntaxHighlightingColors.SINGLE_LINE_COMMENT),
		AttributesDescriptor("Multiline line comment", SquirrelSyntaxHighlightingColors.MULTI_LINE_COMMENT),
		AttributesDescriptor("Identifier", SquirrelSyntaxHighlightingColors.IDENTIFIER),
		AttributesDescriptor("Function call", SquirrelSyntaxHighlightingColors.FUNCTION_CALL),
		AttributesDescriptor("Strings", SquirrelSyntaxHighlightingColors.STRING),
		AttributesDescriptor("Referenced strings", SquirrelSyntaxHighlightingColors.STRING_LINK),
		AttributesDescriptor("Keywords", SquirrelSyntaxHighlightingColors.KEYWORD),
		AttributesDescriptor("Numbers", SquirrelSyntaxHighlightingColors.NUMBER),
		AttributesDescriptor("Operators", SquirrelSyntaxHighlightingColors.OPERATOR),
	)

	override fun getColorDescriptors() = ColorDescriptor.EMPTY_ARRAY


	override fun getAdditionalHighlightingTagToDescriptorMap() = mapOf(
		"keyword" to SquirrelSyntaxHighlightingColors.KEYWORD,
		"fn_call" to SquirrelSyntaxHighlightingColors.FUNCTION_CALL,
		"link" to SquirrelSyntaxHighlightingColors.STRING_LINK,
	)

	override fun getDemoText(): String = """
		<keyword>this</keyword>.typical_skill <- this.<fn_call>inherit</fn_call>(<link>"scripts/skills/skill"</link>, {
			m = {
				Rounds = 0
			},
			// Single line comment
			function create() {
				this.m.ID = "effects.nightmares";
				this.m.Name = "Nightmares";
				this.m.Type = this.Const.SkillType.StatusEffect;
				this.m.IsRemovedAfterBattle = true;
			}
			
			/*
			  Multiline comment
			*/
			function isHidden() {
				local actor = this.<fn_call>getContainer</fn_call>().<fn_call>getActor</fn_call>();
				if (!actor.<fn_call>isPlacedOnMap</fn_call>())
					return true;

				local myTile = this.<fn_call>getContainer</fn_call>().<fn_call>getActor</fn_call>().<fn_call>getTile</fn_call>();
				if (myTile.Properties.Effect == null && myTile.Properties.Effect.Type != "shadows") {
					return true;
				}
				return false;
			}
		});
	""".trimIndent()

}