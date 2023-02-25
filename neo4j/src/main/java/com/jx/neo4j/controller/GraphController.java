package com.jx.neo4j.controller;

import com.jx.neo4j.common.Neo4jUtil;
import com.jx.neo4j.common.Result;
import com.jx.neo4j.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/graph")
public class GraphController {
    @Autowired
    private Neo4jUtil neo4jUtil;
    @Autowired
    private GraphRepository graphRepository;

    //获取节点列表
    @GetMapping("/node")
    public Result getAllNode(){
        List list=graphRepository.getAllNode();
        return Result.succ(list);
    }
    //获取节点列表（带标签）
    @GetMapping("/nodeandlabels")
    public Result getAllNodeAndLabel(){
        List list=graphRepository.getAllNodeAndLabel();
        return Result.succ(list);
    }
    //添加节点 String label,String name
    @PostMapping("/add/node")
    public Result addNode(@RequestBody Data data){
        List list=graphRepository.addNode(data.getLabel(),data.getName());
        return Result.succ(list);
    }
    //更新节点  String label long nodeid String name
    @PostMapping("/update/node")
    public Result updateNode(@RequestBody Data data){
        List list=graphRepository.updateNode(data.getLabel(),data.getNodeId(),data.getProperties(),data.getPropertiesName());
        return Result.succ(list);
    }
    //删除节点 String label,@RequestParam Long nodeid
    @DeleteMapping("/del/node")
    public Result delNode(@RequestBody Data data){
        List list=graphRepository.deleteNode(data.getLabel(),data.getNodeId());
        return Result.succ(list);
    }
    //找到与该节点有关系的节点  String label,@RequestParam long nodeid
    @PostMapping("/search/node")
    public Result getMoreNode(@RequestBody Data data){
        HashMap hashMap=graphRepository.getMoreNode(data.getLabel(),data.getNodeId());
        return Result.succ(hashMap);
    }

    //获取所有标签
    @GetMapping("/label")
    public  Result getAllLabel(){
        List list=graphRepository.getNodesLabel();
        return Result.succ(list);
    }

    //获取所有节点标签
    @GetMapping("/relation")
    public Result getAllRelation(){
        List list=graphRepository.getRelationType();
        return Result.succ(list);
    }
    //添加关系 String label1, String label2, long sourceId, long targetId, String name
    @PostMapping("/add/relation")
    public Result addRelation(@RequestBody Data data){
        List list=graphRepository.addRelation(data.getLabel1(),data.getLabel2(),data.getSourceId(),data.getTargetId(),data.getName());
       return Result.succ(list);
    }
    //更新关系   String label, long rid, String rname
    @PostMapping("/update/relation")
    public Result updateRelation(@RequestBody Data data){
        List list=graphRepository.updateRelation(data.getLabel(),data.getRid(),data.getRname());
        return Result.succ(list);
    }
    //删除关系  String label,long rid
    @DeleteMapping("del/relation")
    public Result delRelation(@RequestBody Data data){

        graphRepository.deleteRelation(data.getLabel(),data.getRid());
        return Result.succ("删除成功");
    }

    //获取所有具体的关系
    @GetMapping("/links")
    public Result getSTNode(){
        List list=graphRepository.getAllRelation();
        return Result.succ(list);
    }

    //获取所有的类别
    @GetMapping("/category")
    public Result getCategory(){
        List list=graphRepository.getCategory();
        return Result.succ(list);
    }
}

class Data{
    String label;
    Long nodeId;
    String name;
    Long rid;
    String rname;
    Long sourceId;
    Long targetId;
    String properties;
    String propertiesName;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getPropertiesName() {
        return propertiesName;
    }

    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    String label1;
    String label2;

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

}
