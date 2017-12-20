package de.fhro.inf.prg3.a11.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.fhro.inf.prg3.a11.R;
import de.fhro.inf.prg3.a11.openmensa.model.Meal;

/**
 * RecyclerView adapter to display meals
 * @author Peter Kurfer
 */

public class MealsRecyclerAdapter extends RecyclerView.Adapter<MealsRecyclerAdapter.MealsItemViewHolder> {

    private final List<Meal> meals;
    private final LayoutInflater inflater;
    private final Activity context;

    /**
     * Default constructor
     * @param context context is required to load view elements
     */
    public MealsRecyclerAdapter(Activity context) {
        this(context, new LinkedList<>());
    }

    /**
     * Constructor overload to initialize internal list directly
     * @param context context is required to load view elements
     * @param meals initial list of meals
     */
    public MealsRecyclerAdapter(Activity context, List<Meal> meals) {
        this.context = context;
        this.meals = Collections.synchronizedList(meals);
        inflater = LayoutInflater.from(context);
    }

    public void addAll(Meal... meals) {
        this.meals.addAll(Arrays.asList(meals));
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    public void addAll(Collection<Meal> meals) {
        this.meals.addAll(meals);
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    public void clear() {
        this.meals.clear();
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    @Override
    public MealsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* inflate the view - i.e. creating a view element by parsing the XML */
        View v = inflater.inflate(R.layout.meal_entry, parent, false);
        return new MealsItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MealsItemViewHolder holder, int position) {
        Meal m = meals.get(position);
        /* update data displayed in view element */
        holder.setMealName(m.getName());
        holder.setMealCategory(m.getCategory());
        holder.setMealNotes(m.getNotes());
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    /**
     * ViewHolder responsible to wrap view element for further usage
     */
    class MealsItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView mealNameView;
        private final TextView mealCategoryView;
        private final TextView mealNotesView;

        /**
         * Default constructor
         * @param itemView view element the view holder has to manage
         */
        MealsItemViewHolder(View itemView) {
            super(itemView);

            mealNameView = itemView.findViewById(R.id.meal_name_view);
            mealCategoryView = itemView.findViewById(R.id.meal_category_view);
            mealNotesView = itemView.findViewById(R.id.meal_notes_view);
        }

        /**
         * Convenience setter
         * @param mealName name of the meal to display
         */
        void setMealName(String mealName) {
            this.mealNameView.setText(mealName);
        }

        /**
         * Convenience setter
         * @param mealCategory category of the meal to display
         */
        void setMealCategory(String mealCategory) {
            this.mealCategoryView.setText(mealCategory);
        }

        void setMealNotes(List<String> notes) {
            if(notes.size() == 0) return;
            StringBuilder builder = new StringBuilder();
            for(String note : notes) {
                builder.append(note)
                        .append(", ");
            }
            builder.setLength(builder.length() - 2);
            mealNotesView.setText(builder.toString());
        }
    }

}
