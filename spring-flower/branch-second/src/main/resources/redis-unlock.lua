-- 锁的key
-- local key = "lock:name"
-- local thread = "qwertyuiop"
-- local id = redis.call('get',key)
-- if(id == thread) then
--     return redis.call('del',key)
-- end
-- return 0

-- 获取锁的key对应的值
local id = redis.call('get', KEYS[1])
-- 比较值是否等于传入的线程标识
if(id == ARGV[1]) then
    -- 相等则删除锁
    return redis.call('del', KEYS[1])
end
-- 不相等则返回0，表示未删除
return 0
