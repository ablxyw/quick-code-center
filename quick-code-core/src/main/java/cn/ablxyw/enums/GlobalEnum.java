package cn.ablxyw.enums;

/**
 * 全局枚举信息
 *
 * @author weiQiang
 * @date 2020-01-10
 */
public enum GlobalEnum {

    /**
     * 全局状态信息
     */
    QuerySuccess("查询成功!"),

    QueryError("查询失败!"),

    InsertSuccess("增加成功!"),

    InsertError("增加失败!"),

    ImportSuccess("导入成功!"),

    ImportError("导入失败!"),

    DataEmpty("数据为空!"),

    DeleteSuccess("删除成功!"),

    DeleteError("删除失败!"),

    FileEmpty("文件信息为空!"),

    DeleteNoSupport("删除不被允许!"),

    UpdateSuccess("更新成功!"),

    UpdateError("更新失败!"),

    SendSuccess("发送成功!"),

    SendError("发送失败!"),

    ServerUsed("服务忙，请稍后重试!"),

    PkIdEmpty("主键ID为空!"),

    ExceptionMessage("发生了错误:%s"),

    MsgOperationSuccess("操作成功"),

    MsgOperationFailed("操作失败"),

    LogSysNoOpen("日志系统未开启"),

    IndexCreateSuccess("索引创建成功!"),

    IndexExist("索引已经存在!"),

    IndexNoExist("索引不存在!"),

    OriFilePathEmpty("原始文件为空或不存在!"),

    TargetFilePathEmpty("目标文件路径为空!"),

    FileTypeNoSupport("文件类型暂时不支持!"),

    FileConvertError("转换文件失败"),

    TestConnectSuccess("测试连接成功!"),

    TestConnectError("测试连接失败,请检查配置!"),

    NoLogin("未登录"),

    NoQuerySQL("请求类型:%s,请求:%s,暂无查询SQL"),

    NoQueryUrl("指标:%s,暂时间无法查询数据接口,请联系管理员!"),

    NoConfigRequest("请求类型:%s,请求:%s,权限不足,无法查询"),

    DataSourceError("请求:%s,切换数据发生错误:%s"),

    DataBaseError("数据源:%s,配置错误,或者连接不可用,请检查"),

    DollarError("指标:%s,查询SQL中含有'$'关键词,请修改为'#'"),

    IllegalSqlError("指标:%s,查询SQL中含非DQL关键词:%s"),

    IllegalInsertSqlError("指标:%s,不是标准的插入数据语法,请检查"),

    IllegalUpdateSqlError("指标:%s,不是标准的修改数据语法,请检查"),

    IllegalDeleteSqlError("指标:%s,不是标准的删除数据语法,且不能包含drop、truncate等关键词,请检查"),

    QuotaTypeError("指标:%s,类型:%s,暂时不支持"),

    RequestParamEmpty("请求参数为空!"),

    OriDataEmpty("该记录不存在,请确认是否存在!"),

    OldPasswordError("数据源:%s,旧密码错误!"),

    PasswordEmpty("密码不能为空!"),

    TokenEmpty("用户授权认证没有通过!客户端请求参数中无token信息"),

    TokenOvertime("token信息已经过期,请重新登陆!"),

    TokenSignError("签名错误"),

    UserLoginOtherIp("您的账号已经在其他终端登陆,建议您重新登陆或修改密码!"),

    UserNameError("帐户名不正确，请重新输入!"),

    PasswordError("登录密码不正确，请重新输入!"),

    UserNoLogin("账号\"%s\"未开启,请联系管理员!"),

    UserNameInUsed("账号\"%s\"已经存在!"),

    AppNameInUsed("应用\"%s\"已经存在!"),

    FrequentlyLogin("账号\"%s\"尝试登陆\"%s\"次,帐户名或登录密码不正确,请稍后再试或联系管理员!"),

    UserLoginNameEmpty("账号为空!"),

    UserPasswordEmpty("密码为空!"),

    UserInfoEmpty("账号\"%s\"不存在!"),

    UriHasInUsed("请求uri已存在"),

    UserRoleEmpty("用户角色不能为空!"),

    NoResetPassword("您非当前登陆用户不能修改密码!"),

    LoginSuccess("登录成功!"),

    LoginError("登录失败!"),

    LogoutSuccess("登出成功!"),

    LogoutError("登出失败!"),

    TokenParamError("参数错误"),

    TokenRequestTimeout("请求过期，请重新请求"),

    HttpConfigHeaderValueEmpty("第三方接口请求头数据为空"),

    HttpHeaderValueEmpty("第三方接口响应头数据为空"),

    ApiNoConfigRequest("第三方接口:'%s',未开启不能查询!"),

    ApiPkEmpty("第三方接口主键为空"),

    ApiConfigEmpty("第三方接口配置信息为空"),

    ApiConfigObtainingNoSupport("请求参数值获取方式:%s,暂时不支持"),

    ApiTargetTableNameEmpty("第三方接口:'%s'目标数据库配置信息为空"),

    ApiFieldEmpty("第三方接口:'%s'结果字段或数据库字段配置信息为空"),

    ApiDataOverrideEmpty("第三方接口:'%s',数据入库模式是覆盖时，覆盖主键不能为空"),

    ApiValueByApiIdEmpty("第三方接口:'%s',参数名:'%s',请求头值通过api获取方式请求接口主键为空"),

    ApiValueByApiKeyEmpty("第三方接口:'%s',参数名:'%s',请求头值通过api获取方式接口Key为空"),

    NoAuthority("对不起您没有该权限!"),

    LoginNoOpen("登录未开启,无需此操作"),

    UserOldPasswordError("旧密码错误!"),

    LastAdminAccount("请保证至少一个管理员账号"),

    DeleteInterfaceUsed("删除不被允许，已经被接口:'%s'所引用"),

    SqlOrApiNotEmpty("指标:\"%s\",SQL、第三方接口、聚合函数至少存在一个"),

    SqlEmpty("指标:\"%s\",SQL不能为空"),

    RequestTimeOut("请求超时!"),

    DataInUsed("\"%s\"已经存在!"),

    DataInNoUsed("\"%s\"不存在!"),

    CacheError("缓存服务器发生异常,请联系管理员!"),
    ;


    private final String message;

    GlobalEnum(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }


}
