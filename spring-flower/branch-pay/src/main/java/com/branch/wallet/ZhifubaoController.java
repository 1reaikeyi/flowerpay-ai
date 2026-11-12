package com.branch.wallet;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.branch.domain.entity.FlowerOrderPay;
import com.branch.domain.enums.PayStatusEnum;
import com.branch.service.FlowerOrderPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import framework.zhifubao.PayDTO;
import framework.zhifubao.ZhifubaoProperties;
import framework.zhifubao.service.ZhifubaoService;
import framework.zhifubao.RefundDTO;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//全局查询修改
@RestController
@RequestMapping("/pay")
@Slf4j
public class ZhifubaoController {

    @Autowired
    private ZhifubaoProperties zhifubaoProperties;

    @Autowired
    private FlowerOrderPayService flowerOrderPayService;


    /** 同步跳转 */
    @GetMapping("/return")
    public String returnUrl() {
        return "已返回商户页面,同步返回,success。";
    }

    /** 异步通知*/
    @PostMapping("/notify")
    public String notifyUrl(HttpServletRequest request) {
        log.info("收到支付宝异步通知");
        Map<String, String> params = new HashMap<>();
        // 1. 转换请求参数
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        // 2. 验签
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    zhifubaoProperties.getAlipayPublicKey(),
                    zhifubaoProperties.getCharset(),
                    zhifubaoProperties.getSignType()
            );

            if (!signVerified) {
                log.error("支付宝异步通知验签失败。params={}", params);
                return "failure";
            }

        //3. 基础字段校验
            String appId = params.get("app_id");
            String outTradeNo = params.get("out_trade_no"); // 商户订单号
            String tradeNo = params.get("trade_no");        // 支付宝交易号
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount"); // 订单金额

            // 校验 app_id 是否匹配
            if (appId == null || !appId.equals(zhifubaoProperties.getAppId())) {
                log.error("通知 app_id 不匹配，预期: {}, 实际: {}", zhifubaoProperties.getAppId(), appId);
                return "failure";
            }
            //4. 业务逻辑处理
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // 先查询数据库，看该订单是否已经处理过
                FlowerOrderPay order = flowerOrderPayService.getById(outTradeNo);
                if (order == null && !PayStatusEnum.PAID.equals(order.getPayStatus())) {
                    log.error("订单不存在！out_trade_no={}", outTradeNo);
                    return "failure";
                }

                // 使用 BigDecimal 进行精确比较
                BigDecimal notifyAmount = new BigDecimal(totalAmount);
                if (notifyAmount.compareTo(order.getAmount()) != 0) {
                    log.error("订单金额不一致！out_trade_no={}, 通知金额={}, 数据库金额={}", outTradeNo, notifyAmount, order.getAmount());
                    return "failure";
                }

                // 建议开启数据库事务，确保状态更新和后续逻辑的一致性
                flowerOrderPayService.lambdaUpdate()
                        .eq(FlowerOrderPay::getId,outTradeNo)
                        .set(FlowerOrderPay::getPayStatus, PayStatusEnum.PAID)
                        .set(FlowerOrderPay::getCheckoutTime, LocalDateTime.now());

                log.info("支付宝异步通知处理成功，订单号: {}, 交易号: {}", outTradeNo, tradeNo);
                return "success";
            }

            // 处理其他状态（如交易关闭）
            log.info("收到非成功状态通知: {}, 订单号: {}", tradeStatus, outTradeNo);

            return "success";
        } catch (Exception e) {
            throw new RuntimeException("处理支付宝异步通知发生异常", e);
//            return "failure";
        }
    }

}

/**
 * 推荐测试顺序（沙箱）
 * ① 下单（页面支付）
 * http://localhost:8080/pay/order?outTradeNo=002&amount=0.01&subject=测试
 * 应返回一段 HTML，并自动跳转到支付宝沙箱收银台；用沙箱买家账号完成支付。
 * ② 交易查询（支付前/后都可试）：
 * http://localhost:8080/pay/query?outTradeNo=TEST20260411001
 * ③ 关单 / 退款（需用 Postman 等发 JSON），例如关单：60min自动，可提前
 * POST http://localhost:8080/pay/close
 * Body（JSON）：{"outTradeNo":"TEST20260411001"}
 * 退款类似：POST /pay/refund，字段与 RefundReq 一致（outTradeNo、refundAmount、refundReason、outRequestNo）。
 * 4. 异步通知 /pay/notify 怎么测
 * 用 内网穿透（ngrok、cpolar 等）把本机映射成公网 HTTPS 地址，并把 notify-url 配成公网地址 + /pay/notify；或
 *     notify-url: http://localhost:8080/pay/notify
 *     return-url: http://localhost:8080/pay/return
 * notify-url: https://你的域名/pay/notify
 * return-url: http://localhost:8080/pay/return（同步仅展示时可继续用本机）
 */
