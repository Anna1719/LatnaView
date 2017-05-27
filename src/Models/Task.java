package Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Created by Ксения Горская on 04.02.2017.
 */
public class Task {
    private Integer id;
    private String time;
    private ArrayList<String[]> listTask;

    public Task() {
        super();
        listTask = new ArrayList<>();
        id=0;
        time="";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String[]> getListTask() {
        return listTask;
    }

//    public ArrayList<String> getListTask() {
//        ArrayList<String> tmp = new ArrayList<>();
//        for(int i=0;i<listTask.size();i++){
//            tmp.add(listTask.get(i)[0]);
//        }
//
//
//        Arrays.sort(tmp.toArray());
//        return tmp;
//    }

    public void setListTask(ArrayList<String[]> listTask) {
        this.listTask = listTask;
    }
}
