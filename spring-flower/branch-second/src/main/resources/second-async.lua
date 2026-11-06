-- KEYS[1]: 库存key      KEYS[2]: 已下单用户集合key      KEYS[3]: 订单Stream key
-- ARGV[1]: 优惠券id      ARGV[2]: 用户id      ARGV[3]: 订单id

local stockKey = KEYS[1]
local orderKey = KEYS[2]
local streamKey = KEYS[3]

local voucherId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

-- 1. 判断库存是否充足（key 不存在时视为 0）
local stock = tonumber(redis.call('get', stockKey))
if (stock == nil or stock <= 0) then
    return 1
end

-- 2. 判断用户是否已下单
if (redis.call('sismember', orderKey, userId) > 0) then
    return 2
end

-- 3. 扣减库存
redis.call('incrby', stockKey, -1)

-- 4. 记录已下单用户
redis.call('sadd', orderKey, userId)

-- 5. 投递订单消息，由消费线程异步落库
redis.call('xadd', streamKey, '*',
        'voucherId', voucherId,
        'userId', userId,
        'orderId', orderId)

return 0
