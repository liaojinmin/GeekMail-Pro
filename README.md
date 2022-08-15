![alt](https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/%E9%80%8Flogo.png)
# 📬GeekMail-高级邮件系统
**地表超强邮件系统**，**多模块**，**高端实用**，**支持邮件模板**，覆盖你能想到的所有邮箱功能 💯
🎉公开售价: **98** 元人民币
🎉楠木星球成员、TrMenu总部星球成员，**免费**
🎉参与插件测试的用户永久免费获得！！！


<br/>
<br/>

## 💽兼容性

| 说明 | 作用 |
| :----: | :----: |
| 支持版本 | 1.12x - 1.18x |
| 可选依赖 | Vault、PlaceholderAPI、PlayerPoints、ItemsAdder |
| 数据储存 | Mysql、Sqlite |

<br/>

---

<br/>

## 📒插件介绍
* 📜基于 **Kotlin / Taboolib** 开发高端邮箱插件(史无前例)
* ♻️邮件模板支持使用 **Kether** 脚本语言
* 💾多种数据储存模式 **MYSQL / SQLITE**
* ❇️超高自由度的 **GUI** 配置，你的邮件想怎么显示就怎么显示 (支持IA)
* ❇️多种邮件类型，可实现礼包形式的系统邮件，自由选择触发事件
* ❇️可绑定方块定点打开邮件，支持一键发送模板邮件，发件是如此简单
* ❇️可权限控制每种邮件的发送，玩家之间可互相邮寄物品等等....
* ❇️使用高端的JSON消息，互动便捷
* ❇️支持 ItemsAdder

<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/%7BRF6T%40AH0Y6J%40JWP6PD%7B9%7D7.png" width="50%">

<br/>

---

<br/>

## ❗❗❗指令帮助
**邮箱打开命令，请自行在菜单配置中设置**
| 玩家命令 | 作用说明 |
| :---------- | :---------- |
| /gkm 或 /mail | 主命令 |
| /gkm mail [邮件种类] [目标] [标题] [内容] [可选参数] | 发送一封指定种类的邮件 |
| /gkm pack [邮件模板] [目标] | 发送配置模板中的邮件给目标玩家 |

> **/gkm mail** 会检查玩家权限以及相应的发送物品需求！
> **/gkm pack** 邮件模板则根据模板中的 **Kether** 条件语句运行判断 **(不支持控制台发送)**
> 除纯 **文本邮件** 以外，其它邮件类型都需要扣除玩家自身的经济或者消耗品。
> **物品类型** 邮件输入完必要参数后，将打开一个GUI，玩家放入需要发送的物品即可发送
{.is-info}

| 管理员命令 | 作用说明 |
| :---------- | :---------- |
| /gkm 或 /mail | 主命令 |
| /gkm send [目标] [种类] [标题] [内容] [可选参数] | 发送一封指定种类的邮件给目标 |
| /gkm reload | 重载插件 |
| /gkm setblock | 设置指定方块为快捷打开邮箱的方块 |
| /gkm global [模板ID] [全局模式] | 为在线的所有玩家发送模板邮件 (不会运行**Kether**脚本) |

> 管理员命令发送任何邮件都不会扣除需求，
{.is-success}

<br/>

---

<br/>

## 🎛️权限帮助
| 权限 | 作用 |
| :---------- | :---------- |
| mail.send.(邮件类型) | 发送各种邮件的权限 |
| mail.command.admin | 管理员权限 |
> **权限问题:** 
> 部分权限未列出，使用命令时，如果没权限后台会提示
{.is-warning}

> **邮件类型:** 
> MONEY_MAIL = 金币邮件
> POINTS_MAIL = 点券邮件
> EXP_MAIL = 经验值邮件
> TEXT_MAIL = 纯文本邮件
> ITEM_MAIL = 物品包邮件
{.is-info}

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
  - '< D F S > '

# 菜单打开命令， 也就是邮件列表的打开命令
Bindings:
  Commands: '邮件'

# 每个图标的设置，当然你可以增加更多，只是注意给他们分配合适的种类
Icons:
# 注意 M 字图标配置已被内定，请勿更改，但是里面的描述信息可以随便改
  M:
    Type: text
    display:
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
      lore:
        - '&0我只是一个挡板'
```
> **图标种类:**
> TEXT = 邮件展示
> DELETE = 删除已读
> LAST_PAGE = 上一页
> NEXT_PAGE = 下一页
> 其余任意字符代表装属，可以随意布局。
{.is-success}

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
    text: |-
      &l
      &B&l 你的好朋友发了一些金币给你哦！
      &B&l 里面有一万大洋哦！
      &B
    type: MONEY_MAIL
    # 如果是物品库，则填写物品库 物品ID
    appendix: 10000
```
| 内置Kether脚本 | 作用 |
| :---------- | :---------- |
| Money (give,take,has,hasTake) [action] | 关于金币的各种脚本 |
| Points (give,take,has,hasTake) [action] | 关于点券的各种脚本 |

> **Money hasTake 100** 判断玩家是否拥有 100 金币，如果有扣除并返回 True
> **Points hasTake 100** 判断玩家是否拥有 100 点券，如果有扣除并返回 True
{.is-success}

> 更多 **Kether** 脚本表达式  [点击前往](https://kether.tabooproject.org/list.html)
{.is-info}



# 🔆更多图片展示
### 上线提醒
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/N%7D%60%29OVA%40I9%5B3TP%25Y3QO%283HF.png" width="50%">

### 附件领取
<img src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/docs/T7PY94_BO116SOHM68XHJSQ.png" width="50%">


# ⚡更新日志
> 202208092101: 版本号 1.06-Beta
> > 1. 修复 CatServer 服务端加载问题
> > 2. 改进物品翻译名称，新增物品邮件预览页面
> > 3. 使用低版本java编译，理论适用 1.7.10-1.19
> > 
> {.is-info}

> 202208112232: 版本号 1.08-Beta
> > 1. 增加全局邮件，向在线的所有玩家发送邮件
> > 2. 适配1.19.2，感谢坏黑dd的高速更新！！！
> > 3. 修复pack指令在控制台执行的报错，现在只能玩家执行
> > 3. 1.13+ 版本增加成就框提示邮件信息！
> > 
> {.is-info}
