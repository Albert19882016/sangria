package sangria.parser

import org.parboiled2.{ParserInput, Position}
import org.scalatest.{Matchers, WordSpec}
import sangria.ast._
import sangria.util.FileUtil

import scala.util.{Failure, Success}

class QueryParserSpec extends WordSpec with Matchers {

  "QueryParser" should {
    "parse complex query" in {
      val query = FileUtil loadQuery "complex-query.graphql"

      val expectedAst =
        Document(
          List(
            OperationDefinition(
              OperationType.Query, 
              Some("FetchLukeAndLeiaAliased"),
              List(
                VariableDefinition(
                  "someVar",
                  Type("Int", false, false, Some(Position(53, 2, 41))),
                  Some(FloatValue(1.23, Some(Position(59, 2, 47)))), Some(Position(43, 2, 31))),
                VariableDefinition(
                  "anotherVar",
                  Type("Int", false, false, Some(Position(77, 2, 65))),
                  Some(IntValue(123, Some(Position(83, 2, 71)))), Some(Position(64, 2, 52)))),
              List(
                Directive(
                  "include",
                  List(Argument("if", BooleanValue(true, Some(Position(100, 2, 88))), Some(Position(96, 2, 84)))),
                  Some(Position(87, 2, 75))),
                Directive(
                  "include",
                  List(Argument("if", BooleanValue(false, Some(Position(119, 2, 107))), Some(Position(115, 2, 103)))),
                  Some(Position(106, 2, 94)))),
              List(
                Field(
                  Some("luke"),
                  "human",
                  List(Argument("id", StringValue("1000", Some(Position(145, 3, 19))), Some(Position(141, 3, 15)))),
                  List(Directive("include", List(Argument("if", BooleanValue(true, Some(Position(165, 3, 39))), Some(Position(161, 3, 35)))), Some(Position(152, 3, 26)))),
                  List(Field(None, "friends", List(Argument("sort", EnumValue("NAME", Some(Position(190, 4, 19))), Some(Position(184, 4, 13)))), Nil, Nil, Some(Position(176, 4, 5)))),
                  Some(Position(129, 3, 3))),
                Field(
                  Some("leia"), "human",
                  List(Argument("id", StringValue("10103\n ö ö", Some(Position(223, 6, 24))), Some(Position(214, 6, 15)))),
                  Nil,
                  List(Field(None, "name", Nil, Nil, Nil, Some(Position(249, 7, 5)))),
                  Some(Position(202, 6, 3))),
                InlineFragment(
                  "User",
                  Nil,
                  List(Field(None, "birth", Nil, Nil, List(Field(None, "day", Nil, Nil, Nil, Some(Position(297, 11, 11)))), Some(Position(291, 11, 5)))),
                  Some(Position(273, 10, 3))),
                FragmentSpread("Foo", Nil, Some(Position(309, 14, 3)))), Some(Position(13, 2, 1))),
            FragmentDefinition(
              "Foo",
              "User",
              List(
                Directive(
                  "foo",
                  List(Argument("bar", IntValue(1, Some(Position(350, 17, 32))), Some(Position(345, 17, 27)))),
                  Some(Position(340, 17, 22)))),
              List(Field(None, "baz", Nil, Nil, Nil, Some(Position(356, 18, 3)))),
              Some(Position(319, 17, 1)))),
          Some(Position(13, 2, 1)))

      QueryParser.parse(query) should be (Success(expectedAst))
    }

    "parse kitchen-sink" in {
      val query = FileUtil loadQuery "kitchen-sink.graphql"

      val expectedAst =
        Document(
          List(
            OperationDefinition(
              OperationType.Query,
              Some("queryName"),
              List(
                VariableDefinition(
                  "foo",
                  Type("ComplexType", false, false, Some(Position(310, 8, 23))),
                  None,
                  Some(Position(304, 8, 17))),
                VariableDefinition(
                  "site",
                  Type("Site", false, false, Some(Position(330, 8, 43))),
                  Some(EnumValue("MOBILE", Some(Position(337, 8, 50)))),
                  Some(Position(323, 8, 36)))),
              Nil,
              List(
                Field(
                  Some("whoever123is"),
                  "node",
                  List(
                    Argument(
                      "id",
                      ArrayValue(List(IntValue(123, Some(Position(373, 9, 27))), IntValue(456, Some(Position(378, 9, 32)))), Some(Position(372, 9, 26))),
                      Some(Position(368, 9, 22)))),
                  Nil,
                  List(
                    Field(
                      None,
                      "id",
                      Nil,
                      Nil,
                      Nil,
                      Some(Position(390, 10, 5))),
                    InlineFragment(
                      "User",
                      List(
                        Directive("defer", Nil, Some(Position(411, 11, 17)))),
                      List(
                        Field(
                          None,
                          "field2",
                          Nil,
                          Nil,
                          List(
                            Field(None, "id", Nil, Nil, Nil, Some(Position(443, 13, 9))),
                            Field(
                              Some("alias"),
                              "field1",
                              List(
                                Argument("first", IntValue(10, Some(Position(476, 14, 29))), Some(Position(470, 14, 23))),
                                Argument("after", VariableValue("foo", Some(Position(486, 14, 39))), Some(Position(480, 14, 33)))),
                              List(
                                Directive(
                                  "include",
                                  List(Argument("if", VariableValue("foo", Some(Position(506, 14, 59))), Some(Position(502, 14, 55)))),
                                  Some(Position(493, 14, 46)))),
                              List(
                                Field(None, "id", Nil, Nil, Nil, Some(Position(524, 15, 11))),
                                FragmentSpread("frag", Nil, Some(Position(538, 16, 11)))),
                              Some(Position(456, 14, 9)))),
                          Some(Position(426, 12, 7)))),
                      Some(Position(399, 11, 5)))),
                  Some(Position(349, 9, 3)))),
              Some(Position(288, 8, 1))),
            OperationDefinition(
              OperationType.Mutation,
              Some("likeStory"),
              Nil,
              Nil,
              List(
                Field(
                  None,
                  "like",
                  List(Argument("story", IntValue(123, Some(Position(612, 24, 15))), Some(Position(605, 24, 8)))),
                  List(Directive("defer", Nil, Some(Position(617, 24, 20)))),
                  List(
                    Field(None, "story", Nil, Nil, List(
                      Field(None, "id", Nil, Nil, Nil, Some(Position(644, 26, 7)))), Some(Position(630, 25, 5)))),
                  Some(Position(600, 24, 3)))),
              Some(Position(577, 23, 1))),
            FragmentDefinition(
              "frag",
              "Friend",
              Nil,
              List(
                Field(
                  None,
                  "foo",
                  List(
                    Argument("size", VariableValue("size", Some(Position(698, 32, 13))), Some(Position(692, 32, 7))),
                    Argument("bar", VariableValue("b", Some(Position(710, 32, 25))), Some(Position(705, 32, 20))),
                    Argument("obj", ObjectValue(List(ObjectField("key", StringValue("value", Some(Position(725, 32, 40))), Some(Position(720, 32, 35)))), Some(Position(719, 32, 34))), Some(Position(714, 32, 29)))),
                  Nil,
                  Nil,
                  Some(Position(688, 32, 3)))),
              Some(Position(660, 31, 1))),
            OperationDefinition(
              OperationType.Query,
              None,
              Nil,
              Nil,
              List(
                Field(
                  None,
                  "unnamed",
                  List(
                    Argument("truthy", BooleanValue(true, Some(Position(758, 36, 19))), Some(Position(750, 36, 11))),
                    Argument("falsey", BooleanValue(false, Some(Position(772, 36, 33))), Some(Position(764, 36, 25)))),
                  Nil,
                  Nil,
                  Some(Position(742, 36, 3))),
                Field(None, "query", Nil, Nil, Nil, Some(Position(782, 37, 3)))),
              Some(Position(738, 35, 1)))),
          Some(Position(288, 8, 1)))
      
      QueryParser.parse(query) should be (Success(expectedAst))
    }

    "provide useful error message (fragement `on`)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        """
          { ...MissingOn }
          fragment MissingOn Type
        """)

      error.formattedError should be(
        """Invalid input 'T', expected On (line 3, column 30):
          |          fragment MissingOn Type
          |                             ^""".stripMargin
      )
    }

    "provide useful error message (braces)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "{ field: {} }")

      error.formattedError should be(
        """Invalid input '{', expected Name (line 1, column 10):
          |{ field: {} }
          |         ^""".stripMargin
      )
    }

    "provide useful error message (operation def)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "notanoperation Foo { field }")

      error.formattedError should be(
        """Invalid input 'n', expected OperationDefinition or FragmentDefinition (line 1, column 1):
          |notanoperation Foo { field }
          |^""".stripMargin
      )
    }

    "provide useful error message (ellipsis)" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "...")

      error.formattedError should be (
        """Invalid input '.', expected OperationDefinition or FragmentDefinition (line 1, column 1):
          |...
          |^""".stripMargin
      )
    }

    "parses constant default values" in {
      QueryParser.parse("{ field(complex: { a: { b: [ $var ] } }) }").isSuccess should be (true)
    }

    "parses variable inline values" in {
      val Failure(error: SyntaxError) = QueryParser.parse(
        "query Foo($x: Complex = { a: { b: [ $var ] } }) { field }")

      error.formattedError should be (
        """Invalid input '$', expected ValueConst or ws (line 1, column 37):
          |query Foo($x: Complex = { a: { b: [ $var ] } }) { field }
          |                                    ^""".stripMargin
      )
    }
  }

}
