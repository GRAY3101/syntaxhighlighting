package highlighting.antlr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AntlrTokenCollectorTest {

  private final AntlrTokenCollector highlighter = new AntlrTokenCollector();

  @Test
  void highlightsTokensFromAntlrLexer() {
    String text = "@A class /*x*/";

    var regions = highlighter.computeRegions(text);

    assertEquals(
        List.of(
            new HighlightRegion(0, 1, MiniJavaColours.ANNOTATION_COLOUR),
            new HighlightRegion(1, 2, MiniJavaColours.ANNOTATION_COLOUR),
            new HighlightRegion(3, 8, MiniJavaColours.KEYWORD_COLOUR),
            new HighlightRegion(9, 14, MiniJavaColours.BLOCK_COMMENT_COLOUR)),
        regions);
  }
}
