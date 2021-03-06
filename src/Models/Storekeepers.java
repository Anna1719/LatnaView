package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Ксения Горская on 20.03.2017.
 */
public class  Storekeepers {
    static Storekeepers instance;
    private Vector<StorekeeperModel> storekeepers;
    public Storekeepers()
    {
        storekeepers = new Vector<>();
    }

    public static Storekeepers getInstance(){return  instance;}
    public void addStorekeeper(StorekeeperModel model){
        storekeepers.add(model);
    }

    public final StorekeeperModel getStorekeeper(int index)
    {
        return storekeepers.get(index);
    }

    public StorekeeperModel[] toStorekeepersArray(){
        StorekeeperModel[] array=new StorekeeperModel[storekeepers.size()];
        for (int i=0;i<storekeepers.size();i++){
            array[i]=storekeepers.get(i);
        }
        return array;
    }

    public String[] getHeaders(){
        return new String[]{"Id","NumberOfTasks","SummaryPath","TimeMoving","TimeSorting","OverallTime","Tasks"};
    }

    public List<String[]> toStringList(){
        List<String[]> list =new ArrayList<>(storekeepers.size());
        for (StorekeeperModel item: storekeepers ) {
            list.add(item.toString().split(";"));
        }
        return list;
    }
}
