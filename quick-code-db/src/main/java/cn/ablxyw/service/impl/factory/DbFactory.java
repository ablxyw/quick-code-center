package cn.ablxyw.service.impl.factory;

import cn.ablxyw.service.AbstractDbService;
import cn.ablxyw.service.impl.factory.impl.MysqlServiceImpl;
import cn.ablxyw.service.impl.factory.impl.OracleServiceImpl;
import cn.ablxyw.service.impl.factory.impl.PostGreServiceImpl;
import org.apache.commons.lang.StringUtils;

/**
 * 数据源Factory
 *
 * @author yxp
 * @date 2021-03-26
 **/
public class DbFactory {

    public static AbstractDbService getDb(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        switch (type) {
            case "oracle":
                return new OracleServiceImpl();
            case "postgresql":
                return new PostGreServiceImpl();
            default:
                //默认mysql
                return new MysqlServiceImpl();
        }
    }

}
