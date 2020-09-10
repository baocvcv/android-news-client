package com.java.baohan.FragmentInterface.EntityInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.java.baohan.R;
import com.java.baohan.backend.KnowledgeGraph;
import com.java.baohan.backend.KnowledgeNode;
import com.java.baohan.backend.KnowledgeRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Fragment for Epidemic map
public class FragmentInterface3 extends Fragment {

    private static FragmentInterface3 INSTANCE = null;

    private FragmentInterface3() {}

    private EditText queryInput;
    private LinearLayout searchResult;
    private TextView searchInfo;
    private View loadingPanel;

    public static FragmentInterface3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface3();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_entity_search, container, false);
        root.findViewById(R.id.search_loading_panel).setVisibility(View.GONE);
        queryInput = root.findViewById(R.id.search_query);
        searchResult = root.findViewById(R.id.search_result);
        searchInfo = root.findViewById(R.id.search_result_info);
        loadingPanel = root.findViewById(R.id.search_loading_panel);

        // set listener
        Button btn = root.findViewById(R.id.search_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = queryInput.getText().toString().trim();
                if (query.isEmpty())
                    return;
                System.out.println(query);

                setLoadingPanelVisible(true);
                new SearchTask().execute(query);
            }
        });

        return root;
    }

    private void updateView(List<KnowledgeNode> result) {
        searchResult.removeAllViews();
        if(result != null) {
            boolean first = true;
            for(KnowledgeNode node: result) {
                searchResult.addView(parseNode(node, first));
                first = first ? !first : first;
            }
        }
    }

    private void updateMst(String msg) {
        searchInfo.setText(msg);
    }

    private void setLoadingPanelVisible(boolean visible) {
        loadingPanel.setVisibility(visible ? View.VISIBLE : View.GONE);
        searchResult.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private View parseNode(KnowledgeNode n, boolean visible) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout entity = (LinearLayout)inflater.inflate(R.layout.entity, null);

        TextView entityName = entity.findViewById(R.id.entity_name);
        String label = n.label;
        for(int i = 0; i < n.hot; i++)
            label += ""; //TODO: find a symbol for fire
        entityName.setText(label);
        TextView entityWiki = entity.findViewById(R.id.entity_wiki);
        if(n.intro != null) {
            entityWiki.setText(n.intro);
        } else {
            entityWiki.setVisibility(View.GONE);
        }
        ImageView img = entity.findViewById(R.id.entity_img);
        if(n.img != null) {
            img.setImageBitmap(n.img);
        } else {
            img.setVisibility(View.GONE);
        }

        LinearLayout relations = entity.findViewById(R.id.relations_list);
        if(n.relations != null) {
            int len = n.relations.size();
            int i = 0;
            List<View> hiddenRelations = new ArrayList<>();
            for(KnowledgeRelation r: n.relations) {
                View rv = parseRelation(inflater, r);
                if (i >= 10) {
                    rv.setVisibility(View.GONE);
                    hiddenRelations.add(rv);
                }
                relations.addView(rv);
                i++;
            }
            TextView more = entity.findViewById(R.id.more_btn);
            if(i >= 10) {
                more.setVisibility(View.VISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = (TextView)view;
                        if(tv.getText().equals("﹀")) {
                            for(View v: hiddenRelations)
                                v.setVisibility(View.VISIBLE);
                            tv.setText("︿");
                        } else {
                            for(View v: hiddenRelations)
                                v.setVisibility(View.GONE);
                            tv.setText("﹀");
                        }
                    }

                });
            } else {
                more.setVisibility(View.GONE);
            }
        } else {
            entity.findViewById(R.id.relation_layout).setVisibility(View.GONE);
        }

        LinearLayout properties = entity.findViewById(R.id.properties_list);
        if(n.properties != null) {
            int i = 0;
            List<View> hiddenRelations = new ArrayList<>();
            for(Map.Entry<String, String> p: n.properties.entrySet()) {

                LinearLayout pv = (LinearLayout)inflater.inflate(R.layout.property, null);
                TextView pname = pv.findViewById(R.id.property_name);
                pname.setText(p.getKey());
                TextView pdes = pv.findViewById(R.id.property_description);
                pdes.setText(p.getValue());
                properties.addView(pv);

                if(i >= 5) {
                    pv.setVisibility(View.GONE);
                    hiddenRelations.add(pv);
                }
                i++;
            }
            TextView more = entity.findViewById(R.id.more_btn2);
            if(i >= 5) {
                more.setVisibility(View.VISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = (TextView)view;
                        if(tv.getText().equals("﹀")) {
                            for(View v: hiddenRelations)
                                v.setVisibility(View.VISIBLE);
                            tv.setText("︿");
                        } else {
                            for(View v: hiddenRelations)
                                v.setVisibility(View.GONE);
                            tv.setText("﹀");
                        }
                    }
                });
            } else {
                more.setVisibility(View.GONE);
            }
        } else {
            entity.findViewById(R.id.property_layout).setVisibility(View.GONE);
        }

        entity.findViewById(R.id.entity_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = entity.findViewById(R.id.relation_layout).getVisibility();
                if(n.intro != null)
                    entity.findViewById(R.id.entity_wiki).setVisibility(8 - v);
                if(n.img != null)
                    entity.findViewById(R.id.entity_img).setVisibility(8 - v);
                if(n.relations != null)
                    entity.findViewById(R.id.relation_layout).setVisibility(8 - v);
                if(n.properties != null)
                    entity.findViewById(R.id.property_layout).setVisibility(8 - v);
            }
        });
        if(visible) {
            if (n.intro != null)
                entity.findViewById(R.id.entity_wiki).setVisibility(View.VISIBLE);
            if (n.img != null)
                entity.findViewById(R.id.entity_img).setVisibility(View.VISIBLE);
            if (n.relations != null)
                entity.findViewById(R.id.relation_layout).setVisibility(View.VISIBLE);
            if (n.properties != null)
                entity.findViewById(R.id.property_layout).setVisibility(View.VISIBLE);
        }
        return entity;
    }

    private View parseRelation(LayoutInflater inflater, KnowledgeRelation r) {
        LinearLayout ret = (LinearLayout)inflater.inflate(R.layout.relation, null);
        TextView relation = ret.findViewById(R.id.relationship);
        relation.setText(r.relation);
        if(r.forward) {
            ret.findViewById(R.id.dir_left).setVisibility(View.GONE);
        } else {
            ret.findViewById(R.id.dir_right).setVisibility(View.GONE);
        }
        TextView entity = ret.findViewById(R.id.relationship_entity_name);
        entity.setText(r.label);
        ret.findViewById(R.id.relation_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingPanelVisible(true);
                queryInput.setText(r.label);
                new SearchTask().execute(r.label);
            }
        });
        return ret;
    }

    private class SearchTask extends AsyncTask<String, String, List<KnowledgeNode>> {
        private long t0;

        @Override
        protected void onPreExecute() {
            t0 = System.currentTimeMillis();
        }

        @Override
        protected List<KnowledgeNode> doInBackground(String... query) {
            return KnowledgeGraph.search(query[0]);
        }

        @Override
        protected void onPostExecute(List<KnowledgeNode> result) {
            double dt = (System.currentTimeMillis() - t0) / 1000.0;
            updateMst(String.format("Found %d results in %.3f seconds.", result == null ? 0 : result.size(), dt));
            setLoadingPanelVisible(false);
            updateView(result);
        }
    }
}
