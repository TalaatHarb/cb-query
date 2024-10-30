package net.talaatharb.query.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JSONSyntaxHighliter {
	
	private static final JsonFactory JSON_FACTORY = new JsonFactory();

	public static final StyleSpans<Collection<String>> computeHighlighting(String text){
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		  try {
		    JsonParser parser = JSON_FACTORY.createParser(text);
		    int lastPos = 0;
		    while (!parser.isClosed()) {
		      JsonToken jsonToken = parser.nextToken();
		      
		      int length = parser.getTextLength();
		      // Because getTextLength() does contain the surrounding ""
		      if(jsonToken == JsonToken.VALUE_STRING || jsonToken == JsonToken.FIELD_NAME) {
		        length += 2;
		      }

		      String className = jsonTokenToClassName(jsonToken);
		      if (!className.isEmpty()) {
		    	    int start = (int) parser.currentTokenLocation().getCharOffset();
		    	    // Fill the gaps, since Style Spans need to be contiguous.
		    	    if(start > lastPos)
		    	    {
		    	        int noStyleLength = start - lastPos;
		    	        spansBuilder.add(Collections.emptyList(), noStyleLength);
		    	    }
		    	    lastPos = start + length;

		    	    spansBuilder.add(Collections.singleton(className), length);
		    	}
		    }
		  } catch (IOException e) {
		    // Ignoring JSON parsing exception in the context of
		    // syntax highlighting
		  }

		  return spansBuilder.create();
	}
	
	private static String jsonTokenToClassName(JsonToken jsonToken) {
		  if (jsonToken == null) {
		    return "";
		  }
		  switch (jsonToken) {
		    case FIELD_NAME:
		      return "keyword";
		    case VALUE_STRING:
		      return "string";
		    case VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT:
		      return "number";
		    default:
		      return "";
		  }
		}
}
