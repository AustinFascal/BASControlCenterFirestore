package com.ptbas.controlcenter.utility;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ptbas.controlcenter.model.GiModel;

import java.util.HashMap;

public class QueryUtils {
    private DatabaseReference databaseReference;
    public QueryUtils()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("GoodIssueData");
    }
    public Task<Void> add(GiModel emp)
    {
        return databaseReference.push().setValue(emp);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {
            return databaseReference.orderByKey().limitToFirst(12);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(12);
    }

    public Query get()
    {
        return databaseReference;
    }
}
