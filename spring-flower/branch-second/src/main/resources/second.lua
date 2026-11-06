-- KEYS[1]: 库存key      KEYS[2]: 已下单用户集合key
-- ARGV[1]: 优惠券id     ARGV[2]: 用户id      ARGV[3]: 订单id

local stockKey = KEYS[1]
local orderKey = KEYS[2]

local voucherId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

-- 1. 判断库存是否充足（key 不存在时视为 0，避免 nil 比较报错）
local stock = tonumber(redis.call('get', stockKey))
if stock == nil or stock <= 0 then
    -- 库存不足，返回1
    return 1
end

-- 2. 判断用户是否已下单（sismember返回1表示已存在，0表示不存在）
if(redis.call('sismember',orderKey,userId) = 1) then
    -- 用户已下单，返回2
    return 2
end

-- 3. 扣减库存
redis.call('incrby',stockKey,-1)

-- 4. 将用户加入订单集合
redis.call('sadd',orderKey,userId)

-- 5. 下单成功，返回0
return 0
