package com.sematext.solr.redis.command;

import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import java.util.Map;

public class SUnion implements Command {
  private static final Logger log = LoggerFactory.getLogger(SUnion.class);

  @Override
  public Map<String, Float> execute(Jedis jedis, String key, SolrParams params) {
    final String[] keys = ParamUtil.getStringByPrefix(params, "key");

    log.debug("Fetching SUNION from Redis for keys: {}", keys);

    return ResultUtil.stringIteratorToMap(jedis.sunion(keys));
  }
}