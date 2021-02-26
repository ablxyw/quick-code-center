package cn.ablxyw.convert;


import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * bpm模型转为Json
 *
 * @author weiqiang
 * @date 2021-02-25 上午10:18
 */
@Slf4j
public class BpmConvert {

    /**
     * 转换模块
     *
     * @param filePath 文件位置
     * @return ObjectNode
     */
    public static ObjectNode convertJsonNode(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return convertJsonNode(file);
        }
        return null;
    }

    /**
     * 转换模块
     *
     * @param filePath 文件
     * @return ObjectNode
     */
    public static ObjectNode convertJsonNode(File filePath) {
        log.info("开始转换模型:{}", filePath);
        ObjectNode modelNode = null;
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xtr;
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader in = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            xtr = xif.createXMLStreamReader(in);
            BpmnModel model = new BpmnXMLConverter().convertToBpmnModel(xtr);
            BpmnJsonConverter converter = new BpmnJsonConverter();
            modelNode = converter.convertToJson(model);
        } catch (Exception e) {
            log.error("转换bpm模型失败:{}", e.getMessage());
            return null;
        }
        return modelNode;
    }
}
