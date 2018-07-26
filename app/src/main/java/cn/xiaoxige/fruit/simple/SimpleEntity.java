package cn.xiaoxige.fruit.simple;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 */

public class SimpleEntity {

    private int id;
    private String massger;

    public SimpleEntity() {
    }

    public SimpleEntity(int id, String massger) {
        this.id = id;
        this.massger = massger;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMassger() {
        return massger;
    }

    public void setMassger(String massger) {
        this.massger = massger;
    }
}

