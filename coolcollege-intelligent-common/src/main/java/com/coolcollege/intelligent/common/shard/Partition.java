package com.coolcollege.intelligent.common.shard;

import lombok.Data;

/**
 * @Description 分区对象
 * @author Aaron
 * @date 2019/12/20
 */
@Data
public abstract class Partition {

    /**
     * 分区表总数
     */
    protected int tableCount;

    /**
     * 分区表表名前缀
     */
    protected String tablePrefix;


    /**
     * 根据id获取分区表
     *
     * @param id
     * @return
     */
    public Table getTable(String id) {
        Hashing hashing = new MurmurHash();
        Long key = Long.valueOf(hashing.hash(id));
        int mod = Math.abs((int) (key % tableCount));
        String tableName = tablePrefix + "_" + mod;
        Table table = new Table();
        table.setTableName(tableName);
        return table;
    }


    public static void main(String[] args) {
        Hashing hashing = new MurmurHash();
        Long key = Long.valueOf(hashing.hash("927478455925346314"));
        int mod = Math.abs((int) (key % 64));
        System.out.println(mod);
    }

}
