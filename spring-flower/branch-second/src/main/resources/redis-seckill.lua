-- 优惠券id对应的库存key: voucherSecond:stock:voucherId
-- 订单key使用set存储已下单用户: voucherSecond:order:voucherId

-- 获取参数：优惠券id
local voucherId = ARGV[1]
-- 获取参数：用户id
local userId = ARGV[2]
-- 获取参数：订单id
local orderId = ARGV[3]

-- 定义key
local stockKey = "voucherSecond:stock:" .. voucherId
local orderKey = "voucherSecond:order:" .. voucherId

-- 1. 判断库存是否充足
if(tonumber(redis.call('get',stockKey)) <= 0) then
    -- 库存不足，返回1
    return 1
end

-- 2. 判断用户是否已下单（sismember返回1表示已存在，0表示不存在）
if(redis.call('sismember',orderKey,userId) > 0) then
    -- 用户已下单，返回2
    return 2
end

-- 3. 扣减库存
redis.call('incrby',stockKey,-1)

-- 4. 将用户加入订单集合
redis.call('sadd',orderKey,userId)

-- 5. 下单成功，返回0
redis.call('xadd','stream.order','*','voucherId',voucherId,'userId',userId,'orderId',orderId)
return 0
