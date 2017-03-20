/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.phantom.example.advanced

import java.util.UUID

import com.datastax.driver.core.{ResultSet, Row}
import com.outworkers.phantom.connectors.RootConnector
import com.twitter.conversions.time._
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.example.basics.Recipe
import com.outworkers.phantom.{CassandraTable => _, _}
import org.joda.time.DateTime

import scala.concurrent.{Future => ScalaFuture}

/**
 * In this example we will create a  table storing recipes.
 * This time we will use a composite key formed by name and id.
 */
// You can seal the class and only allow importing the companion object.
// It's not directly meant for end user consumption anyway, the correct approach
// Keep reading for examples.
abstract class AdvancedRecipes extends CassandraTable[AdvancedRecipes, Recipe] with RootConnector {
  // First the partition key, which is also a Primary key in Cassandra.
  object id extends UUIDColumn(this) with PartitionKey {
    // You can override the name of your key to whatever you like.
    // The default will be the name used for the object, in this case "id".
    override lazy  val name = "the_primary_key"
  }

  object name extends StringColumn(this)

  object title extends StringColumn(this)
  object author extends StringColumn(this)
  object description extends StringColumn(this)

  // Custom data types can be stored easily.
  // Cassandra collections target a small number of items, but usage is trivial.
  object ingredients extends SetColumn[String](this)
  object props extends MapColumn[String, String](this)
  object timestamp extends DateTimeColumn(this) with ClusteringOrder

  // Like in the real world, you have now planned your queries ahead.
  // You know what you can do and what you can't based on the schema limitations.
  def findById(id: UUID): ScalaFuture[Option[Recipe]] = {
    select.where(_.id eqs id).one()
  }
}
