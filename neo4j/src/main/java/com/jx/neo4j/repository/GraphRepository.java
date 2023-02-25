package com.jx.neo4j.repository;

import com.jx.neo4j.common.Result;
import com.jx.neo4j.entity.GraphQuery;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface GraphRepository {

    //获取所有数据
    List getData(String cql);

    //获取具体的关系 源节点 目标节点
    List getAllRelation();

    //获取节点列表
    List getAllNode();

    List getCategory();

    //查询所有节点标签
    List getNodesLabel();

    //获取所有节点及标签
    List getAllNodeAndLabel();

    //获取所有关系类型
    List getRelationType();

    //添加节点
    List addNode(String label,String name);

    //更新节点
    List updateNode(String label,long nodeid,String properties,String propertiesName);

    //找到和该节点有关系的节点
    HashMap getMoreNode(String label,long nodeid);

    //添加关系
    List addRelation(String label1,String label2,long sourceId,long targetId,String rname);

    //更新关系
    List updateRelation(String label,long rid,String rname);

    //删除关系
    void deleteRelation(String label,long rid);

    //删除节点，先删除关系再删除节点
    //返回删除的节点信息
    List deleteNode(String label,long nodeid);

}
