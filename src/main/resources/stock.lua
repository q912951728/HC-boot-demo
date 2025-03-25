if redis.call('exists', KEYS[1]) == 1 and tonumber(redis.call('get', KEYS[1])) > 0 then
   return redis.call('decr', KEYS[1]) -- 直接减少库存，并返回剩余库存
else
   return -1 -- 表示库存不足
end
