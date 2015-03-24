/**
 * 
 */
package com.josue.square;

/**
 * @author jtorres
 *
 */
import java.util.ArrayList;

public interface RequestListener extends ErrorListener {

    public void onVenuesFetched(ArrayList<Venue> venues);

}