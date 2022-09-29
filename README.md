![alt](https://web-1301331373.cos.ap-guangzhou.myqcloud.com/mail/mail-b-gq.png)
# 📬GeekMail-Pro  高级邮件系统 ^2.02^
- **地表超强邮件系统**，**多模块**，**高端实用**，**支持邮件模板**，覆盖你能想到的所有邮箱功能 💯
- **公开售价:** **158** 元人民币
- **购买联系:** QQ1349517404 (回应迅速)
- **爱发电购买:** [点击前往](https://afdian.net/item?plan_id=e9f2a7a0172511edabfd52540025c377) 
- **BUG及功能建议:** [点击前往](https://github.com/liaojinmin/GeekMail-Pro/issues)

- 🎉楠木星球成员、TrMenu总部星球成员，**免费**
- 🎉参与插件测试的用户永久免费获得！！！
- 🎉最新版本已适配 1.19.2 服务端

<br/>

---
## 💽兼容性

| 说明 | 作用 |
| :----: | :----: |
| 支持版本 | 1.12x - 1.19x |
| 可选依赖 | Vault、PlaceholderAPI、PlayerPoints、ItemsAdder、TrHologram |
| 数据储存 | Mysql、Sqlite |

<br/>

---
## 📒插件介绍
- 📜基于 **Kotlin / Taboolib** 开发高端邮箱插件(史无前例)
- ♻邮件模板支持使用 **Kether** 脚本语言
- 💾多种数据储存模式 **MYSQL / SQLITE**
- ❇️超高自由度的 **GUI** 配置，你的邮件想怎么显示就怎么显示 (支持IA)
- ❇️多种邮件类型，可实现礼包形式的系统邮件
- ❇️可绑定方块定点打开邮件，支持一键发送模板邮件，发件是如此简单
- ❇️可权限控制每种邮件的发送，玩家之间可互相邮寄物品等等....
- ❇️支持使用任意消息互动 (全自定义消息种类)
- ❇️支持使用 SMTP 服务 发送真实邮件通知玩家
- ❇️支持 ItemsAdder
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/%7BRF6T%40AH0Y6J%40JWP6PD%7B9%7D7.png" width="50%">

<br/>

---

## ❗❗❗指令帮助
**邮箱打开命令，请自行在菜单配置中设置**

| 玩家命令 | 作用说明 |
| :---------- | :---------- |
| /gkm 或 /mail | 主命令 |
| /gkm mail [邮件种类] [目标] [标题] [内容] [可选参数] | 发送一封指定种类的邮件 |
| /gkm pack [邮件模板] [目标] | 发送配置模板中的邮件给目标玩家 |

- **/gkm mail** 会检查玩家权限以及相应的发送物品需求！
- **/gkm pack** 邮件模板则根据模板中的 **Kether** 条件语句运行判断 **(不支持控制台发送)**
- 除纯 **文本邮件** 以外，其它邮件类型都需要扣除玩家自身的经济或者消耗品。
- **物品类型** 邮件输入完必要参数后，将打开一个GUI，玩家放入需要发送的物品即可发送

| 管理员命令 | 作用说明 |
| :---------- | :---------- |
| /gkm 或 /mail | 主命令 |
| /gkm send [目标] [种类] [标题] [内容] [可选参数] | 发送一封指定种类的邮件给目标 |
| /gkm reload | 重载插件 |
| /gkm setblock | 设置指定方块为快捷打开邮箱的方块 |
| /gkm global [模板ID] [全局模式] | 为在线的所有玩家发送模板邮件 (不会运行**Kether**脚本) |

> 管理员命令发送任何邮件都不会扣除需求，

<br/>

---

## 🎛️权限帮助

| 权限 | 作用 |
| :---------- | :---------- |
| mail.command.pack | 发送模板邮件权限 |
| mail.command.admin | 管理员权限 |
**权限问题:** 
> 部分权限未列出，使用时，如果没权限后台会提示


**内置邮件类型:** 
- MAIL_MONEY = 金币邮件
- MAIL_POINTS = 点券邮件
- MIAL_EXP = 经验值邮件
- MAIL_TEXT = 纯文本邮件
- MAIL_ITEM = 物品包邮件
- MAIL_CMD = 指令包邮件

<br/>

---

# 🔊如何设置 SMTP 服务?
点击链接: https://g.pconline.com.cn/x/892/8926470.html 随后在配置中设置必要信息
``` YML
SmtpSet:
  start: false
  account: '****r@163.com' # 发件账号
  password: '*********' # smtp服务器授权码
  personal: 'GeekMail-高级邮件系统' # 发件人信息
  subjects: 'GeekMail-收件提醒' # 邮件标题
  host: 'smtp.163.com' # smtp服务器
  port: '25' # 端口
```
<br/>

---

# 🔊如何改变默认菜单界面？
在 **plugins/GeekMail/menu** 文件夹下修改def.yml
如果你使用过 **TrMenu** 你将会得心应手
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/TXQAICR8SYXX%7B0G4DRA%7B2_5.png" width="50%"><img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/1A_YM08K%252_93SA%29%5BS%7DZ3%24G.png" width="42%">
``` YAML
# 菜单展示标题
TITLE: '&0&l邮件系统'
TYPE: main

# 菜单布局 ， 在这里改变菜单的排版
Layout:
  - '#########'
  - '#MMMMMMM#'
  - '#MMMMMMM#'
  - '#MMMMMMM#'
  - '#########'
  - '< D P S > '

# 菜单打开命令， 也就是邮件列表的打开命令
Bindings:
  Commands: '邮件'

# 每个图标的设置，当然你可以增加更多，只是注意给他们分配合适的种类
Icons:
# 注意 M 字图标配置已被内定，请勿更改，但是里面的描述信息可以随便改
  M:
    Type: text
    display:
      # mats: 'IA:BOOK'  使用 ItemsAdder 格式
      mats: BOOK
      name: '&7主题: &f[title]'
      lore:
        - ''
        - ' &7发件人: &f[sender]'
        - ' &7邮件种类: &f[type]'
        - ' &7邮件内容:'
        - '    [text]'
        - ''
        - ' &7&l附件: [state]'
        - ' &f[item]'
        - ''
        - '&8[&f✧&8] &8[&B&l左键点击&8] &7# 领取附件'
        - '&8[&f✧&8] &8[&B&l右键点击&8] &7# 删除邮件'
        - '&8[&f✧&8] &8[&B&lShift_点击&8] &7# 预览物品附件'

  'P':
    Type: BIND
    display:
      mats: SPECTRAL_ARROW
      name: '&3# &b绑定真实邮箱'
      lore:
        - ' &7你的绑定: &f[mail_Info]'
        - ''
        - ' &7支持的邮箱:'
        - ' &fQQ邮箱: &7xxxxx@qq.com'
        - ' &f网易邮箱: &7xxxxx@163.com'
        - ' &f谷歌邮箱: &7xxxxx@gmail.com'
        - ''
        - '&8[&f✧&8] &8[&B&l左键_点击&8] &7# 开始绑定'
  '<':
  # 图标种类 LAST_PAGE 代表这上一个 上一页 的按钮， 
    Type: LAST_PAGE
    display:
      mats: IRON_DOOR
      name: '&3# &b跳转至'
      lore:
        - '&7上一页'
  '>':
  # 图标种类 NEXT_PAGE 代表这上一个 下一页 的按钮， 
    Type: NEXT_PAGE
    display:
      mats: IRON_DOOR
      name: '&3# &b跳转至'
      lore:
        - '&7下一页'
        
  # 图标种类 DELETE 代表这是一个删除已读邮件的按钮
  'D':
    Type: DELETE
    display:
      mats: CAULDRON
      name: '&3# &C删除已读邮件'
      lore:
        - ''
        - '&8[&f✧&8] &8[&B&l右键点击&8] &7# 删除已读邮件'
        
  # 图标种类标记为 null 代表这是一个装饰图标，你可以增加更多的装饰图标
  '#':
    Type: null
    display:
      mats: BLACK_STAINED_GLASS_PANE
      data: 0
      name: '&b&l 挡板'
```
**图标种类:**
- TEXT = 邮件展示
- DELETE = 删除已读
- LAST_PAGE = 上一页
- NEXT_PAGE = 下一页
- BIND = 绑定按钮
- 其余任意字符代表装属，可以随意布局。

<br/>

---

# 🔊如何创建一个邮件模板？
在 **plugins/GeekMail/template** 文件夹下新建任意名字的 **.yml** 配置
``` YAML
Template:
  # 模板唯一展示ID
  ID: "金币大礼包"
  Require:
    # 发送该邮件前判断条件 需要扣除玩家 100 点券
    condition: 'Points hasTake *100'
    # 条件通过 发送 Kether Tell 消息
    action: |-
      tell "&a"
      tell "&7&l  Geek&f&lMail &8>&7>&f>"
      tell "&a"
      tell "&B    发送成功 扣除 100 点券！"
      tell "&a"
      
    # 条件不通过 发送 Kether Tell 消息
    
    deny: |-
      tell "&a"
      tell "&7&l  Geek&f&lMail &8>&7>&f>"
      tell "&a"
      tell "&c    你无法使用这个邮件模板"
      tell "&a"
      

  package:
    title: "&e金币包"
    # 在文本中可使用 ;  分割换行
    text: |-
      &l
      &B&l 你的好朋友发了一些金币给你哦;
      &B&l 里面有一万大洋哦！
      &B
    type: MONEY_MAIL
    appendix:
      additional: 10000 # 附件参数 数字类型 如果不是数字类邮件类型请设置为 0
      items:
       # - 'material:PAPER,name:&f白纸的名字(可选),lore:&b&l这是一张白纸(可选)' # 原版物品
       # - 'IA:custom_items' # ItemsAdder物品
      command:

       # - 'say 执行了一个服务器消息' #执行的指令
```
## 模板物品特征
- 材质; material
- 名称: name
- 描述: lore
- 数量: amount
- 损伤值: data
- 模型数据: ModelData
> material:PAPER,name:&f白纸的名字(可选),lore:&b&l这是一张白纸(可选)

| 内置Kether脚本 | 作用 |
| :---------- | :---------- |
| Money (give,take,has,hasTake) [action] | 关于金币的各种脚本 |
| Points (give,take,has,hasTake) [action] | 关于点券的各种脚本 |
| Exp (give,take,has,hasTake) [action] | 关于经验的各种脚本 |

- **Money hasTake 100** 判断玩家是否拥有 100 金币，如果有扣除并返回 True
- **Points hasTake 100** 判断玩家是否拥有 100 点券，如果有扣除并返回 True
- **Exp hasTake 100** 判断玩家是否拥有 100 经验值，如果有扣除并返回 True
> 更多 **Kether** 脚本表达式  [点击前往](https://kether.tabooproject.org/list.html)


<br/>

---

# 🔊开发者API
**package** me.Geek.GeekMail.api.mail
``` JAVA 
me.Geek.GeekMail.api.mail.event.MailReceiveEvent  // 邮件接收事件
me.Geek.GeekMail.api.mail.event.MailSenderEvent  // 邮件发送事件
me.Geek.GeekMail.api.mail.event.WebMailSenderEvent // 真实邮件发送事件
me.Geek.GeekMail.api.mail.MailManage  // 邮件管理、注册
me.Geek.GeekMail.api.mail.MailSub // 邮件模板扩展
```



# 🔆更多图片展示
### 上线提醒
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/N%7D%60%29OVA%40I9%5B3TP%25Y3QO%283HF.png" width="50%">

### 附件领取
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/T7PY94_BO116SOHM68XHJSQ.png" width="50%">
