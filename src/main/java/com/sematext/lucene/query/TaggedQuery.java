package com.sematext.lucene.query;

import java.io.IOException;
import java.util.Objects;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

/**
 * This query is used to tag original query.
 *
 * Query wraps original one and adds metadata to it.
 * @author prog
 */
public class TaggedQuery extends Query {

  /**
   * Wrapped query which is tagged by internal tag field
   */
  private final Query wrappedQuery;

  /**
   * Tag of this query
   */
  private final String tag;

  /**
   * Tagged query gets two parameters in constructor. It is used to tag the given query with
   * the given label.
   *
   * @param query query
   * @param tag String tag
   */
  public TaggedQuery(final Query query, final String tag) {
    this.wrappedQuery = query;
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  public Query getWrappedQuery() {
    return wrappedQuery;
  }

  @Override
  public Query rewrite(final IndexReader reader) throws IOException {
    return wrappedQuery.rewrite(reader);
  }

  @Override
  public String toString(final String field) {
    return "TaggedQuery [" + wrappedQuery + "]";
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.wrappedQuery);
    hash = 79 * hash + Objects.hashCode(this.tag);
    return hash;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TaggedQuery other = (TaggedQuery) obj;
    return Objects.equals(this.wrappedQuery, other.wrappedQuery) && Objects.equals(this.tag, other.tag);
  }

}
