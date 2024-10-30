package net.talaatharb.query.service;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SQLSyntaxHighliter {
	
	private static final String[] SQL_KEYWORDS = new String[] { "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE",
			"CREATE", "DROP", "ALTER", "JOIN", "ON", "AS", "ANY", "AND", "BETWEEN", "CONSTRAINT", "INDEX", "DESC", "ASC", "EXISTS" };

	private static final String SQL_KEYWORD_PATTERN = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final String NUMBER_PATTERN = "\\b\\d+\\b";

	private static final Pattern SQL_PATTERN = Pattern.compile("(?<KEYWORD>" + SQL_KEYWORD_PATTERN + ")" + "|(?<STRING>"
			+ STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")" + "|(?<NUMBER>" + NUMBER_PATTERN + ")");

	public static final StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = SQL_PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		while (matcher.find()) {
			String styleClass = switch (matcher) {
			case Matcher m when m.group("KEYWORD") != null -> "keyword";
			case Matcher m when m.group("STRING") != null -> "string";
			case Matcher m when m.group("COMMENT") != null -> "comment";
			case Matcher m when m.group("NUMBER") != null -> "number";
			default -> null;
			};

			assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
