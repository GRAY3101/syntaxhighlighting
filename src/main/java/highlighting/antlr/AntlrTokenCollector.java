package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

// TODO Phase III — AntlrTokenCollector (token-based syntax highlighting).

// This highlighter uses the ANTLR-generated MiniJavaLexer to turn the input text into a token
// stream. {@code collectMatches(String)} is the only method you need to implement: extract tokens
// of interest and map them to {@code HighlightRegions} using the colours from {@code
// MiniJavaColours}. Sorting, filtering of invalid regions, and conflict handling are performed by
// the base class {@code SyntaxHighlighter} via the template method {@code computeRegions(...)}.
public class AntlrTokenCollector extends SyntaxHighlighter {

  // TODO (Phase III — implement this method): Use the token stream produced by the ANTLR-generated
  // {@code MiniJavaLexer} to collect highlight regions.
  //
  // Requirements / hints:
  // - Iterate over the lexer tokens (typically via {@code CommonTokenStream}); ignore the EOF
  // token.
  // - For each token type that should be coloured (e.g., keywords, string/char literals, comments),
  // create a {@code HighlightRegion} with the corresponding colour from {@code MiniJavaColours}.
  // - Use {@code Token#getStartIndex()} and {@code Token#getStopIndex()} (inclusive) to compute
  // {@code [start, end)} ranges: {@code start = startIndex, end = stopIndex + 1}.
  // - Do not sort, merge, or resolve overlaps here; return all candidates as you find them.
  // Normalisation and conflict resolution are handled later by the template method.
  // - Annotation highlighting: colour '@' and the immediately following IDENTIFIER token (if
  // present).
  @Override
  public List<HighlightRegion> collectMatches(String text) {
    var lexer = new MiniJavaLexer(CharStreams.fromString(text));
    var stream = new CommonTokenStream(lexer);
    stream.fill();

    var tokens = stream.getTokens();
    var regions = new ArrayList<HighlightRegion>();

    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      int type = token.getType();

      if (type == Token.EOF) {
        continue;
      }

      Color colour = colourFor(type);
      if (colour != null) {
        regions.add(new HighlightRegion(token.getStartIndex(), token.getStopIndex() + 1, colour));
      }

      if (type == MiniJavaLexer.AT && i + 1 < tokens.size()) {
        Token next = tokens.get(i + 1);
        if (next.getType() == MiniJavaLexer.IDENTIFIER
            && next.getStartIndex() == token.getStopIndex() + 1) {
          regions.add(
              new HighlightRegion(
                  next.getStartIndex(),
                  next.getStopIndex() + 1,
                  MiniJavaColours.ANNOTATION_COLOUR));
        }
      }
    }

    return regions;
  }

  private Color colourFor(int tokenType) {
    return switch (tokenType) {
      case MiniJavaLexer.STRING_LITERAL -> MiniJavaColours.STRING_LITERAL_COLOUR;
      case MiniJavaLexer.CHAR_LITERAL -> MiniJavaColours.CHAR_LITERAL_COLOUR;
      case MiniJavaLexer.PACKAGE,
          MiniJavaLexer.IMPORT,
          MiniJavaLexer.CLASS,
          MiniJavaLexer.PUBLIC,
          MiniJavaLexer.PRIVATE,
          MiniJavaLexer.FINAL,
          MiniJavaLexer.RETURN,
          MiniJavaLexer.NULL,
          MiniJavaLexer.NEW,
          MiniJavaLexer.IF,
          MiniJavaLexer.ELSE,
          MiniJavaLexer.WHILE,
          MiniJavaLexer.EXTENDS,
          MiniJavaLexer.IMPLEMENTS ->
          MiniJavaColours.KEYWORD_COLOUR;
      case MiniJavaLexer.LINE_COMMENT -> MiniJavaColours.LINE_COMMENT_COLOUR;
      case MiniJavaLexer.JAVADOC_COMMENT -> MiniJavaColours.JAVADOC_COMMENT_COLOUR;
      case MiniJavaLexer.BLOCK_COMMENT -> MiniJavaColours.BLOCK_COMMENT_COLOUR;
      case MiniJavaLexer.AT -> MiniJavaColours.ANNOTATION_COLOUR;
      default -> null;
    };
  }
}
