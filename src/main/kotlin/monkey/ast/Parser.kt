package monkey.ast

import monkey.ast.exceptions.InvalidTokenException
import monkey.ast.exceptions.UnexpectedTokenException
import monkey.ast.expressions.*
import monkey.ast.statements.*
import monkey.lexer.Lexer
import monkey.token.Token
import monkey.token.TokenType

typealias PrefixParseFunction = () -> Expression
typealias InfixParseFunction = (argument: Expression) -> Expression

val PREFERENCES_MAP = mapOf(
        TokenType.EQ to Precedence.EQUALS,
        TokenType.NOT_EQ to Precedence.EQUALS,
        TokenType.LT to Precedence.LESSGREATER,
        TokenType.GT to Precedence.LESSGREATER,
        TokenType.PLUS to Precedence.SUM,
        TokenType.MINUS to Precedence.SUM,
        TokenType.SLASH to Precedence.PRODUCT,
        TokenType.ASTERISK to Precedence.PRODUCT,
        TokenType.LPAREN to Precedence.CALL,
        TokenType.LBRACKET to Precedence.INDEX)

/**
 * @author andrea
 * @since 7/18/17
 */
class Parser(private val lexer: Lexer) {

    private val parsePrefixExpression: PrefixParseFunction = {
        val token = currentToken
        val operator = currentToken.literal

        nextToken()

        PrefixExpression(token, operator, parseExpression(Precedence.PREFIX))
    }

    private val parseInfixExpression: InfixParseFunction = {
        val token = currentToken
        val operator = currentToken.literal
        val left = it

        val precedence = currentPrecedence()
        nextToken()

        InfixExpression(token, operator, left, parseExpression(precedence))
    }

    private val parseGroupedExpression: PrefixParseFunction = {
        nextToken()

        val expression = parseExpression(Precedence.LOWEST)
        if (!expectPeek(TokenType.RPAREN))
            throw UnexpectedTokenException(TokenType.LPAREN, peekToken.tokenType)

        expression
    }

    private val parseIfExpression: PrefixParseFunction = {
        val ifToken = currentToken

        if (!expectPeek(TokenType.LPAREN))
            throw UnexpectedTokenException(TokenType.LPAREN, peekToken.tokenType)

        nextToken()
        val condition = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RPAREN))
            throw UnexpectedTokenException(TokenType.RPAREN, peekToken.tokenType)

        if (!expectPeek(TokenType.LBRACE))
            throw UnexpectedTokenException(TokenType.RBRACE, peekToken.tokenType)

        val consequence = parseBlockStatement()

        if (peekToken.tokenType == TokenType.ELSE) {
            nextToken()

            if (!expectPeek(TokenType.LBRACE))
                throw UnexpectedTokenException(TokenType.RBRACE, peekToken.tokenType)

            IfExpression(ifToken, condition, consequence, parseBlockStatement())
        } else {
            IfExpression(ifToken, condition, consequence)
        }
    }

    private val parseFunctionLiteralExpression: PrefixParseFunction = {
        val fnToken = currentToken

        if (!expectPeek(TokenType.LPAREN))
            throw UnexpectedTokenException(TokenType.LPAREN, peekToken.tokenType)

        val parameters = parseFunctionParameters()

        if (!expectPeek(TokenType.LBRACE))
            throw UnexpectedTokenException(TokenType.LBRACE, peekToken.tokenType)

        FunctionLiteralExpression(fnToken, parameters, parseBlockStatement())
    }

    private val parseCallExpression: InfixParseFunction = {
        CallExpression(currentToken, it, parseExpressionList(TokenType.RPAREN))
    }

    private val parseIndexExpression: InfixParseFunction = {
        val token = currentToken

        nextToken()

        val index = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RBRACKET))
            throw UnexpectedTokenException(TokenType.LBRACE, peekToken.tokenType)

        IndexExpression(token, it, index)
    }

    private val parseHashLiteralExpression: PrefixParseFunction = {
        val hashToken = currentToken
        val pairs = mutableMapOf<Expression, Expression>()

        while (peekToken.tokenType != TokenType.RBRACE) {
            nextToken()
            val key = parseExpression(Precedence.LOWEST)

            if (!expectPeek(TokenType.COLON))
                throw UnexpectedTokenException(TokenType.COLON, peekToken.tokenType)

            nextToken()
            val value = parseExpression(Precedence.LOWEST)

            pairs[key] = value

            if (peekToken.tokenType != TokenType.RBRACE && !expectPeek(TokenType.COMMA))
                throw UnexpectedTokenException(TokenType.RBRACE, peekToken.tokenType)
        }

        if (!expectPeek(TokenType.RBRACE))
            throw UnexpectedTokenException(TokenType.RBRACE, peekToken.tokenType)

        HashLiteralExpression(hashToken, pairs)
    }

    private val PREFIX_PARSE_FUNCTIONS: Map<TokenType, PrefixParseFunction> = mapOf(
            TokenType.IDENT to { IdentifierExpression(this.currentToken, this.currentToken.literal) },
            TokenType.INT to { IntegerLiteralExpression(this.currentToken, this.currentToken.literal.toInt()) },
            TokenType.STRING to { StringLiteralExpression(this.currentToken, this.currentToken.literal) },
            TokenType.LBRACKET to { ArrayLiteralExpression(currentToken, parseExpressionList(TokenType.RBRACKET)) },
            TokenType.TRUE to { BooleanExpression(this.currentToken, true) },
            TokenType.FALSE to { BooleanExpression(this.currentToken, false) },
            TokenType.BANG to parsePrefixExpression,
            TokenType.MINUS to parsePrefixExpression,
            TokenType.LPAREN to parseGroupedExpression,
            TokenType.IF to parseIfExpression,
            TokenType.FUNCTION to parseFunctionLiteralExpression,
            TokenType.LBRACE to parseHashLiteralExpression)
    private val INFIX_PARSE_FUNCTIONS: Map<TokenType, InfixParseFunction> = mapOf(
            TokenType.EQ to parseInfixExpression,
            TokenType.NOT_EQ to parseInfixExpression,
            TokenType.LT to parseInfixExpression,
            TokenType.GT to parseInfixExpression,
            TokenType.PLUS to parseInfixExpression,
            TokenType.MINUS to parseInfixExpression,
            TokenType.SLASH to parseInfixExpression,
            TokenType.ASTERISK to parseInfixExpression,
            TokenType.LPAREN to parseCallExpression,
            TokenType.LBRACKET to parseIndexExpression)

    private var currentToken: Token = this.lexer.nextToken()
    private var peekToken: Token = this.lexer.nextToken()

    fun parseProgram(): Program {
        val statements = mutableListOf<Statement>()

        while (currentToken.tokenType != TokenType.EOF) {
            statements.add(parseStatement())
            nextToken()
        }

        return Program(statements)
    }

    private fun parseStatement() = when (currentToken.tokenType) {
        TokenType.LET -> parseLetStatement()
        TokenType.RETURN -> parseReturnStatement()
        else -> parseExpressionStatement()
    }

    private fun parseExpressionStatement(): ExpressionStatement {
        val expression = parseExpression(Precedence.LOWEST)

        if (peekToken.tokenType == TokenType.SEMICOLON)
            nextToken()

        return ExpressionStatement(currentToken, expression)
    }

    private fun parseExpression(precedence: Precedence): Expression {
        val parsePrefixExpression = PREFIX_PARSE_FUNCTIONS[currentToken.tokenType] ?:
                throw InvalidTokenException(currentToken)

        var leftExpression = parsePrefixExpression()

        while (peekToken.tokenType != TokenType.EOF && precedence < peekPrecedence()) {
            val parseInfixExpression = INFIX_PARSE_FUNCTIONS[peekToken.tokenType] ?: return leftExpression

            nextToken()

            leftExpression = parseInfixExpression(leftExpression)
        }

        return leftExpression
    }

    private fun parseLetStatement(): LetStatement {
        val letToken = currentToken

        if (!expectPeek(TokenType.IDENT))
            throw UnexpectedTokenException(TokenType.IDENT, peekToken.tokenType)

        val name = IdentifierExpression(currentToken, currentToken.literal)

        if (!expectPeek(TokenType.ASSIGN))
            throw UnexpectedTokenException(TokenType.ASSIGN, peekToken.tokenType)

        nextToken()

        val value = parseExpression(Precedence.LOWEST)

        if (peekToken.tokenType == TokenType.SEMICOLON)
            nextToken()

        return LetStatement(letToken, name, value)
    }

    private fun parseReturnStatement(): ReturnStatement {
        val returnToken = currentToken

        nextToken()

        val returnValue = parseExpression(Precedence.LOWEST)

        if (peekToken.tokenType == TokenType.SEMICOLON)
            nextToken()

        return ReturnStatement(returnToken, returnValue)
    }

    private fun parseBlockStatement(): BlockStatement {
        val statements = mutableListOf<Statement>()
        val blockToken = currentToken

        nextToken()

        while (currentToken.tokenType != TokenType.RBRACE && currentToken.tokenType != TokenType.EOF) {
            statements.add(parseStatement())
            nextToken()
        }

        return BlockStatement(blockToken, statements)
    }

    private fun parseFunctionParameters(): List<IdentifierExpression> {
        nextToken()

        if (currentToken.tokenType == TokenType.RPAREN)
            return emptyList()

        val identifiers = mutableListOf<IdentifierExpression>()

        identifiers.add(IdentifierExpression(currentToken, currentToken.literal))

        while (peekToken.tokenType === TokenType.COMMA) {
            nextToken()
            nextToken()

            identifiers.add(IdentifierExpression(currentToken, currentToken.literal))
        }

        if (!expectPeek(TokenType.RPAREN))
            throw UnexpectedTokenException(TokenType.RPAREN, peekToken.tokenType)

        return identifiers
    }

    private fun parseExpressionList(endTokenType: TokenType): List<Expression> {
        if (peekToken.tokenType == TokenType.RPAREN) {
            nextToken()
            return emptyList()
        }

        val elements = mutableListOf<Expression>()

        nextToken()

        elements.add(parseExpression(Precedence.LOWEST))

        while (peekToken.tokenType == TokenType.COMMA) {
            nextToken()
            nextToken()
            elements.add(parseExpression(Precedence.LOWEST))
        }

        if (!expectPeek(endTokenType))
            throw UnexpectedTokenException(endTokenType, peekToken.tokenType)

        return elements
    }

    private fun peekPrecedence() = PREFERENCES_MAP[peekToken.tokenType] ?: Precedence.LOWEST

    private fun currentPrecedence() = PREFERENCES_MAP[currentToken.tokenType] ?: Precedence.LOWEST

    private fun expectPeek(type: TokenType) = if (peekToken.tokenType == type) {
        nextToken()
        true
    } else false

    private fun nextToken() {
        this.currentToken = this.peekToken
        this.peekToken = this.lexer.nextToken()
    }
}

