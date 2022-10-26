package me.geek.mail.scheduler.redis

/**
 * 作者: 老廖
 * 时间: 2022/10/19
 *
 **/
enum class RedisMessageType {

    /**
     * 玩家跨服 - 没什么鸟用
     */
    PLAYER_CROSS_SERVER,

    /**
     * 跨服邮件
     */
    CROSS_SERVER_MAIL,

    /**
     * 市场新加商品
     */
    MARKET_ADD,

    /**
     * 市场商品删除
     */
    MARKET_REM,

}