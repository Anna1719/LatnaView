package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Ксения Горская on 20.03.2017.
 */
public class Storekeepers {
    private Vector<StorekeeperModel> storekeepers;

    public Storekeepers()
    {
        storekeepers = new Vector<>();
    }

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

    public List<String[]> toStringList(){
        List<String[]> list =new ArrayList<>(storekeepers.size());
        for (StorekeeperModel item: storekeepers ) {
            list.add(item.toString().split(";"));
        }
        return list;
    }
}
