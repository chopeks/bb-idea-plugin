package com.chopeks.psi.index

import com.intellij.util.indexing.ID

object Indexes {
	val Inheritance = ID.create<String, String>("${javaClass.packageName}.InheritanceIndex")
}