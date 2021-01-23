package org.bytediff.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DiffTest {

  @Test
  public void single_insert_middle(){
    DiffInfo diff = compute("quickfox", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("brown"), diff.getInserts());
    Assertions.assertIterableEquals(List.of(4), diff.getInsertIndexes());
  }

  @Test
  public void single_insert_beginning(){
    DiffInfo diff = compute("fox", "quickfox");

    Assertions.assertIterableEquals(List.of("quick"), diff.getInserts());
    Assertions.assertIterableEquals(List.of(-1), diff.getInsertIndexes());
  }

  @Test
  public void single_insert_end(){
    DiffInfo diff = compute("quick", "quickfox");

    Assertions.assertIterableEquals(List.of("fox"), diff.getInserts());
    Assertions.assertIterableEquals(List.of(4), diff.getInsertIndexes());
  }

  @Test
  public void single_delete_middle(){
    DiffInfo diff = compute("quickbrownfox", "quickfox");

    Assertions.assertIterableEquals(List.of("brown"), diff.getDeletions());
    Assertions.assertIterableEquals(List.of(5), diff.getDeletionIndexes());
  }

  @Test
  public void single_delete_beginning(){
    DiffInfo diff = compute("quickbrownfox", "brownfox");

    Assertions.assertIterableEquals(List.of("quick"), diff.getDeletions());
    Assertions.assertIterableEquals(List.of(0), diff.getDeletionIndexes());
  }

  @Test
  public void single_delete_end(){
    DiffInfo diff = compute("quickbrownfox", "quickbrown");

    Assertions.assertIterableEquals(List.of("fox"), diff.getDeletions());
    Assertions.assertIterableEquals(List.of(10), diff.getDeletionIndexes());
  }

  @Test
  public void single_match_whole(){
    DiffInfo diff = compute("quickbrownfox", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("quickbrownfox"), diff.getMatches());
    Assertions.assertIterableEquals(List.of(0), diff.getMatchIndexes());
  }

  @Test
  public void single_match_middle(){
    DiffInfo diff = compute("brown", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("brown"), diff.getMatches());
    Assertions.assertIterableEquals(List.of(0), diff.getMatchIndexes());
  }

  @Test
  public void single_match_beginning(){
    DiffInfo diff = compute("quickbrown", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("quickbrown"), diff.getMatches());
    Assertions.assertIterableEquals(List.of(0), diff.getMatchIndexes());
  }

  @Test
  public void single_match_end(){
    DiffInfo diff = compute("brownfox", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("brownfox"), diff.getMatches());
    Assertions.assertIterableEquals(List.of(0), diff.getMatchIndexes());
  }

  @Test
  public void single_replace_middle(){
    DiffInfo diff = compute("quickXXXXXfox", "quickbrownfox");

    Assertions.assertIterableEquals(List.of("brown"), diff.getReplacements());
    Assertions.assertIterableEquals(List.of(5), diff.getReplacementIndexes());
  }

  private DiffInfo compute(String source, String target){
    char[] sourceC = source.toCharArray();
    char[] targetC = target.toCharArray();

    return Diff.compute(sourceC, targetC);
  }

}