package model;

/*
 * Code influced by the JsonSerizalizationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
*/

import org.json.JSONObject;

/*
 * Represents a class that can be written into JSON object
 */

public interface Writeable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
