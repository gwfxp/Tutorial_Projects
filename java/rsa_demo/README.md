# RSA Demo
用于演示Java中使用RSA证书加解码的基本操作

1). RSA的相关背景介绍
参考URL: https://baike.baidu.com/item/RSA%E7%AE%97%E6%B3%95/263310?fromtitle=RSA&fromid=210678

1.1). RSA是什么？
    RSA公钥加密算法是1977年由Ron Rivest、Adi Shamirh和LenAdleman在（美国麻省理工学院）开发的。
    RSA取名来自开发他们三者的名字。
    RSA是目前最有影响力的公钥加密算法，它能够抵抗到目前为止已知的所有密码攻击，已被ISO推荐为公钥数据加密标准。
    目前该加密方式广泛用于网上银行、数字签名等场合。
    RSA算法基于一个十分简单的数论事实：将两个大素数相乘十分容易，但那时想要对其乘积进行因式分解却极其困难，因此可以将乘积公开作为加密密钥。

1.2). OpenSSL是什么?
    众多的密码算法、公钥基础设施标准以及SSL协议，或许这些有趣的功能会让你产生实现所有这些算法和标准的想法，但是当你表示敬佩的同时，还是忍不住提醒你：这是一个令人望而生畏的过程。
    这个工作不再是简单的读懂几本密码学专著和协议文档那么简单，而是要理解所有这些算法、标准和协议文档的每一个细节，并用你可能很熟悉的C语言字符一个一个去实现这些定义和过程。
    我们不知道你将需要多少时间来完成这项有趣而可怕的工作，但肯定不是一年两年的问题。
    OpenSSL就是由Eric A. Young和Tim J. Hudson两位绝世大好人自1995年就开始编写的集合众多安全算法的算法集合。通过命令或者开发库，我们可以轻松实现标准的公开算法应用。

1.3). RSA的缺点
    产生密钥很麻烦，受到素数产生技术的限制，因而难以做到一次一密。
    速度太慢，由于RSA 的分组长度太大，为保证安全性，n 至少也要 600 bits以上，使运算代价很高，尤其是速度较慢，较对称密码算法慢几个数量级；且随着大数分解技术的发展，这个长度还在增加，不利于数据格式的标准化。SET(Secure Electronic Transaction）协议中要求CA采用2048比特长的密钥，其他实体使用1024比特的密钥。为了速度问题，人们广泛使用单，公钥密码结合使用的方法，
    优缺点互补：单钥密码加密速度快，人们用它来加密较长的文件，然后用RSA来给文件密钥加密，极好的解决了单钥密码的密钥分发问题。
    (简单来说，RSA一般用于加密其他加密算法中的加密密钥，并用于网络上分发)


2). OpenSSL来生成对应的密匙文件
    $openssl version -a  --> 验证机器上已经安装了OpenSSL的版本

    # 生成文件名为“rsa_private_key.pem”的密匙文件, 加密长度可以为1024或2048
    # 密钥文件最终将数据通过Base64编码进行存储
    # 生成的密匙内容都是标准的ASCII字符，开头一行和结尾一行有明显的标记
    # 而真正的私钥数据是中间的不规则字符。
    # 
    # RFC2045中规定：
    # Base64编码的数据每行最多不超过76字符，对于超长数据需要# // 按行分割
    # PKCS#1格式(非JAVA中常用)
    $openssl genrsa -out rsa_private_key.pem 2048

    # 根据私钥生成公钥
    $openssl rsa -in rsa_private_key.pem -out rsa_public_key.pem -pubout  
writing RSA key  

    # 一般在使用前会将将私钥进行 PKCS#8编码(JAVA中常用) (不采用任何二次加密（-nocrypt）)
    $openssl pkcs8 -topk8 -in rsa_private_key.pem -out pkcs8_rsa_private_key.pem -nocrypt  

    # 不过如果对接对方是要求只能使用第一步生成的未经过PKCS#8编码的私钥文件
    # 则可以在程序中转换获取:
    #   1). 首先将PKCS#1的私钥文件读取出来（注意去掉减号开头的注释内容）
    #   2). 然后使用Base64解码读出的字符串，便得到priKeyData，也就是第一行代码中的参数。
        3). 最后一行得到了私钥
    RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(priKeyData));  
RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());  
KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
PrivateKey priKey= keyFactory.generatePrivate(rsaPrivKeySpec);  

3). 网上可使用的现成工具
    支付宝提供一键生成工具便于开发者生成一对RSA密钥，下载密钥生成工具：
    Doc Ref URL: https://docs.open.alipay.com/291/105971
    WINDOWS: http://p.tb.cn/rmsportal_6680_secret_key_tools_RSA_win.zip
    MAC_OSX: http://p.tb.cn/rmsportal_6680_secret_key_tools_RSA_macosx.zip

4). Java中使用的“The Legion of the Bouncy Castle”包
    Home: http://bouncycastle.org/latest_releases.html
    Maven Repository: 搜索org.bouncycastle

Important Note:
    1. 通过加载文件中的密匙字符串时，需要删除换行符，同时删除 -- Begin / End --等字符串

参考文献：
    RSA介绍：http://baike.baidu.com/view/7520.htm
    OpenSSL介绍：http://baike.baidu.com/view/300712.htm
    密钥对生成：http://www.howforge.com/how-to-generate-key-pair-using-openssl
    私钥编码格式转换：http://shuany.iteye.com/blog/730910
    JCE介绍：http://baike.baidu.com/view/1855103.htm

