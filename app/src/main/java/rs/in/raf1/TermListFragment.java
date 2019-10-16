package rs.in.raf1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class TermListFragment extends Fragment {

    // private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mTermRecyclerView;
    private TermAdapter mAdapter;
    // private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onTermSelected(Term term);
        void onTermDeleted(UUID termId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term_list, container, false);

        mTermRecyclerView = (RecyclerView) view
                .findViewById(R.id.term_recycler_view);
        mTermRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI(TermListActivity.sFilter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(TermListActivity.sFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_term_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);

        menu.findItem(R.id.new_term).setVisible(true);

        // menu.findItem(R.id.show_subtitle).setVisible(true);
        menu.findItem(R.id.search).setVisible(true);

        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);

        sv.setMaxWidth(350);
        sv.setQuery(TermListActivity.sFilter, true);

        sv.setIconified(false);
        sv.clearFocus();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                //updateUI(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
               //sv.clearFocus();

               return true;
            }

            @Override
            public boolean onQueryTextChange(String filter) {
                TermListActivity.sFilter = filter;
                updateUI(TermListActivity.sFilter);
                updateSubtitle(TermListActivity.sFilter);

                /*

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment fragment = getFragmentManager().findFragmentById(R.id.detail_fragment_container);

                if(fragment != null) {
                    ft.remove(fragment);
                    ft.commit();
                }

                */

                return true;
            }
        });


        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                TermListActivity.sFilter = "";
                sv.setQuery("", true);
                return false;
            }
        });

        /*

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }


        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_term:
                Term term = new Term();
                TermLab.get(getActivity()).addTerm(term);
                updateUI(TermListActivity.sFilter);
                mCallbacks.onTermSelected(term);
                TermListActivity.sFilter = "";
                return true;
            /*
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle("");
                return true;

             */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(String filter) {
        TermLab termLab = TermLab.get(getActivity());
        int termCount = termLab.getTerms(filter).size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, termCount, termCount);

        /*
         if (!mSubtitleVisible) {
            subtitle = null;
        }
        */

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI(String filter) {
        TermLab termLab = TermLab.get(getActivity());
        List<Term> terms;

       terms = termLab.getTerms(filter);

        if (mAdapter == null) {
            mAdapter = new TermAdapter(terms);
            mTermRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTerms(terms);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle(filter);
    }

    private class TermHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public Term mTerm;

        private TextView mEnglishTextView;
        private TextView mSerbianTextView;
        //private TextView mDateTextView;
        //private ImageView mSolvedImageView;

        public TermHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_term, parent, false));
            itemView.setOnClickListener(this);

            mEnglishTextView = (TextView) itemView.findViewById(R.id.termEnglish);
            mSerbianTextView = (TextView) itemView.findViewById(R.id.termSerbian);
            //mDateTextView = (TextView) itemView.findViewById(R.id.termDate);
            //mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);



        }


        public void bind(Term term) {
            mTerm = term;
            mEnglishTextView.setText(mTerm.getEnglish());
            mSerbianTextView.setText(mTerm.getSerbian());

            //mDateTextView.setText(mTerm.getDate().toString());
           // mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onTermSelected(mTerm);
            mCallbacks.onTermDeleted(mTerm.getId());
        }
    }

    private class TermAdapter extends RecyclerView.Adapter<TermHolder>{

        private List<Term> mTerms;

        public TermAdapter(List<Term> terms) {
            mTerms = terms;
        }

        @Override
        public TermHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TermHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(final TermHolder holder, final int position) {
            Term term = mTerms.get(position);
            holder.bind(term);
        }

        @Override
        public int getItemCount() {
            return mTerms.size();
        }

        public void setTerms(List<Term> terms) {
            mTerms = terms;
        }
    }
}
