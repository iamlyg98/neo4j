/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compatibility.v3_4.runtime.compiled.codegen.ir

import org.neo4j.cypher.internal.compatibility.v3_4.runtime.compiled.codegen.ir.expressions.CodeGenExpression
import org.neo4j.cypher.internal.compatibility.v3_4.runtime.compiled.codegen.spi.MethodStructure
import org.neo4j.cypher.internal.compatibility.v3_4.runtime.compiled.codegen.{CodeGenContext, Variable}

case class SeekNodeById(opName: String, nodeVar: Variable, expression: CodeGenExpression, action: Instruction) extends Instruction {

  override def body[E](generator: MethodStructure[E])(implicit context: CodeGenContext) = {
    generator.trace(opName) { body =>
      body.incrementDbHits()
      body.nodeIdSeek(nodeVar.name, expression.generateExpression(body), expression.codeGenType) { seekBody =>
        seekBody.incrementRows()
        action.body(seekBody)
      }
    }
  }

  override protected def children: Seq[Instruction] = Seq(action)

  override def init[E](generator: MethodStructure[E])(implicit context: CodeGenContext) = {
    super.init(generator)
    expression.init(generator)
  }
}
