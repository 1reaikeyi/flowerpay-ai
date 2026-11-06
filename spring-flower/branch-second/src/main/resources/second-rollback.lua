-- KEYS[1]: 库存key      KEYS[2]: 已下单用户集合key
-- ARGV[1]: 用户id


local stockKey = KEYS[1]
local orderKey = KEYS[2]
local userId = ARGV[1]

-- 1. 归还库存
redis.call('incrby', stockKey, 1)

-- 2. 移除已下单标记，允许用户重试
redis.call('srem', orderKey, userId)

return 0
