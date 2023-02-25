package com.jx.neo4j.repository.impl;

import com.jx.neo4j.common.Neo4jUtil;
import com.jx.neo4j.repository.GraphRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class GraphRepositoryImpl implements GraphRepository{

    @Autowired
    private Neo4jUtil neo4jUtil;

    //执行cql语句
    @Override
    public List getData(String cql) {
        List list=neo4jUtil.getEntityList(cql);
        return list;
    }

    //获取具体的关系 源节点 目标节点
    @Override
    public List getAllRelation(){
        String cql="match (n)-[r]->(m) return id(r) as ID,n.name as source,m.name as target,type(r) as type";
        List list=neo4jUtil.getGraphItem(cql);
        System.out.println(list);
        List list2=new ArrayList();
        for(int i=0;i<list.size();i++){
            Object obj=list.get(i);
            String str=obj.toString();

            String s="";
            int flag=1;
            List list1=new ArrayList();
            for(int j=0;j<str.length();j++){
                //前两个条件分离出id，后面的条件依次分离后面的内容
                if(str.charAt(j)=='='&&flag==1 || str.charAt(j)==','&&flag==2 || str.charAt(j)=='\"'){
                    if(str.charAt(j)=='=') flag++;
                    if(str.charAt(j)==',') flag++;
                    list1.add(j);
                }
            }

            HashMap hashMap=new HashMap();
            String str_id=str.substring((int) list1.get(0)+1,(int) list1.get(1));
            String str_source=str.substring((int) list1.get(2)+1,(int) list1.get(3));
            String str_type=str.substring((int) list1.get(4)+1,(int) list1.get(5));
            String str_target=str.substring((int) list1.get(6)+1,(int) list1.get(7));
            hashMap.put("id",Integer.parseInt(str_id));
            hashMap.put("source",str_source);
            hashMap.put("type",str_type);
            hashMap.put("target",str_target);
            list2.add(hashMap);
        }
//        System.out.println(list2);
        return list2;
    }

    //获取所有节点
    @Override
    public List getAllNode() {
        String cql="match (n) return n";
        List list=neo4jUtil.getGraphNode(cql);
        System.out.println(list);
        return list;
    }

    //获取所有节点名字及标签
    @Override
    public List getAllNodeAndLabel() {
        String cql="match (n) return n.name,labels(n)";
        List list=neo4jUtil.getGraphItem(cql);
        List list2=new ArrayList();
        for(int i=0;i<list.size();i++){
            String str=list.get(i).toString();
            List list1=new ArrayList();
            for(int j=0;j<str.length();j++){
                if(str.charAt(j)=='\"'){
                    list1.add(j);
                }
            }

            String str1=str.substring((int) list1.get(0)+1,(int) list1.get(1));
            String str2=str.substring((int) list1.get(2)+1,(int) list1.get(3));
            HashMap hashMap=new HashMap();
            hashMap.put("name",str2);
            hashMap.put("labels",str1);
            list2.add(hashMap);
        }

        return list2;
    }

    @Override
    public List getCategory() {
        String cql="match (n:`联赛`) return n";
        List list=neo4jUtil.getEntityList(cql);
        String cql1="match (n:`球队`) return n";
        List list1=neo4jUtil.getEntityList(cql1);

        List list2=new ArrayList();
        for(int i=0;i<list.size();i++){
            list2.add(list.get(i));
        }
        for(int i=0;i<list1.size();i++){
            list2.add(list1.get(i));
        }
        return list2;
    }

    //获取所有的标签
    @Override
    public List getNodesLabel() {
        String cql = " CALL db.labels(); ";
        return neo4jUtil.getNodesLabelsEntityList(cql);
    }

    //获取所有的关系类型，只有类型，没有 源节点与目标节点
    @Override
    public List getRelationType() {
        String cql="CALL db.relationshipTypes();";
        return neo4jUtil.getRelationshipTypeEntityList(cql);
    }

    //添加节点
    @Override
    public List addNode(String label,String name) {
        String cql=String.format("create (n: `%s` {name:'%s'}) return n",label,name);
        List list=neo4jUtil.getGraphNode(cql);
        return list;
    }

    //更新节点
    @Override
    public List updateNode(String label,long nodeid,String properties,String propertiesName) {
//        String _properties="n."+properties;
        String cql=String.format("MATCH (n:`%s`) where id(n)=%s set n.%s='%s' return n",label,nodeid,properties,propertiesName);
        List list=neo4jUtil.getGraphNode(cql);
        return list;
    }

    //获取更多节点
    @Override
    public HashMap getMoreNode(String label,long nodeid) {
        String cql=String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s  return *",label,nodeid);
        HashMap hashMap=neo4jUtil.getGraphNodeAndShip(cql);

        //如果为空，证明该节点是单独的节点
        if(hashMap.isEmpty()){
            String cql1=String.format("MATCH (n:`%s`) where id(n)=%s  return n",label,nodeid);
            hashMap=neo4jUtil.getGraphNodeAndShip(cql1);
        }
        return hashMap;
    }

    //添加关系
    @Override
    public List addRelation(String label1,String label2, long sourceId, long targetId, String rname) {
        String cql=String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
                + "CREATE (n)-[r:RE{name:'%s'}]->(m) RETURN r",label1,label2,sourceId,targetId,rname);
        List list=neo4jUtil.getGraphRelationShip(cql);
        return list;
    }

    //更新关系
    @Override
    public List updateRelation(String label,long rid,String rname){
        String cql=String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s set r.name='%s' return r",label,rid,rname);
        List list=neo4jUtil.getGraphRelationShip(cql);
        return list;
    }

    //删除关系
    @Override
    public void deleteRelation(String label, long rid) {
        String cql = String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s delete r",label,rid);
        neo4jUtil.excuteCypherSql(cql);
    }

    //删除节点，如果节点有关系的话，先删除关系
    @Override
    public List deleteNode(String label, long nodeid) {
        String ncql = String.format("MATCH (n:`%s`)  where id(n)=%s return n",label, nodeid);
        List list = neo4jUtil.getGraphNode(ncql);
        String rcql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s return r",label, nodeid);
        neo4jUtil.getGraphRelationShip(rcql);
        String deleteRelationSql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s delete r",label, nodeid);
        neo4jUtil.excuteCypherSql(deleteRelationSql);
        String deleteNodeSql = String.format("MATCH (n:`%s`) where id(n)=%s delete n", label, nodeid);
        neo4jUtil.excuteCypherSql(deleteNodeSql);
        return list;
    }
}

