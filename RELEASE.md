# 打包说明
1. 在idea maven中激活release配置文件，然后点击quick-code(root)->Lifecycle->deploy完成打包预发布
2. 登录maven中央仓库，在`Staging Repositories`下将本次发布的包先`close`,执行结束再`release`完成发布

```shell script
   $ gpg2 --gen-key
    
   # 接下来会要求输入 username 和 email，请对号入座，确认信息之后会弹出一个 shell 对话框，要求输入签名秘钥passphrase，输入一个自己记得住的秘钥，一定要记下来，以后每次 deploy 都会使用到。确认后完成签名的生成。
    
   #pub   rsa2048 2019-04-25 [SC] [expires: 2023-02-264]
   #      028E40AEDE712E4D3A0821D78F77EBFAB5F
   #uid                      weiq0525 <weiq0525@gmail.com>>
   #sub   rsa2048 2019-04-25 [E] [expires: 2023-02-26]
    
   # 其中028E40AEDE712E4D3A0821D78F77EBFAB5F 这个是我们后面要用到的公钥key
   # 上传 GPG 公钥到秘钥服务器
   $ gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys 028E40AEDE712E4D3A0821D78F77EBFAB5F
    
   # 通过下面的命令验证是否上传成功
   $ gpg2 --keyserver hkp://pool.sks-keyservers.net --recv-keys 028E40AEDE712E4D3A0821D78F77EBFAB5F
```

