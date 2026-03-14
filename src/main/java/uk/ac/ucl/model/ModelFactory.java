package uk.ac.ucl.model;

import java.io.IOException;

// This class gives access to the model to any other class that needs it.
// Calling the static method getModel (i.e., ModelFactory.getModel()) returns
// an initialised Model object. This version limits the program to one model object,
// which is returned whenever getModel is called.

public class ModelFactory
{
    private static Model model;

    public static Model getModel() throws IOException
    {
        if (model == null)
        {
            model = new Model();
        }
        return model;
    }

    public static void reset()
    {
        model = null;
    }
}
