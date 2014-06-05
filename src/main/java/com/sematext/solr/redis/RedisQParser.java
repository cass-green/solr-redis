package com.sematext.solr.redis;

import java.util.Collection;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

/**
 * RedisQParser is responsible for preparing a query based on data fetched from Redis.
 */
public class RedisQParser extends QParser {
  static final Logger log = LoggerFactory.getLogger(RedisQParserPlugin.class);

  private final JedisCommands redis;
  private Collection<String> terms = null;

  RedisQParser (String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req,
          JedisCommands redis) {
    super(qstr, localParams, params, req);
    this.redis = redis;

    String redisMethod = localParams.get("method");
    String redisKey = localParams.get("key");

    if (redisMethod == null) {
      log.error("No method argument passed to RedisQParser.");
      throw new IllegalArgumentException("No method argument passed to RedisQParser.");
    }

    if (redisKey == null || redisKey.isEmpty()) {
      log.error("No key argument passed to RedisQParser");
      throw new IllegalArgumentException("No key argument passed to RedisQParser");
    }

    if (redisMethod.compareToIgnoreCase("smembers") == 0) {
      log.debug("Fetching smembers from Redis for key: " + redisKey);
      terms = redis.smembers(redisKey);
    }
  }

  @Override
  public Query parse() throws SyntaxError {
    String fieldName = localParams.get(QueryParsing.V, null);
    BooleanQuery booleanQuery = new BooleanQuery(true);
    log.debug("Preparing a query for " + terms.size() + " terms");
    for (String term : terms) {
      TermQuery termQuery = new TermQuery(new Term(qstr, term));
      booleanQuery.add(termQuery, BooleanClause.Occur.SHOULD);
    }
    return booleanQuery;
  }
}