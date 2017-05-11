package pl.itcrowd.seam3.persistence;

import javax.el.ValueExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class QueryParser {
// ------------------------------ FIELDS ------------------------------

    private StringBuilder ejbqlBuilder;

    private List<ValueExpression> parameterValueBindings = new ArrayList<ValueExpression>();

// -------------------------- STATIC METHODS --------------------------

    public static String getParameterName(int loc)
    {
        return "el" + (loc + 1);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public QueryParser(String ejbql, EntityQuery.MyExpressions expressions)
    {
        this(ejbql, 0, expressions);
    }

    public QueryParser(String ejbql, int startingParameterNumber, EntityQuery.MyExpressions expressions)
    {
        StringTokenizer tokens = new StringTokenizer(ejbql, "#}", true);
        ejbqlBuilder = new StringBuilder(ejbql.length());
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ("#".equals(token) && tokens.hasMoreTokens()) {
                String expressionToken = tokens.nextToken();

                if (!expressionToken.startsWith("{") || !tokens.hasMoreTokens()) {
                    ejbqlBuilder.append(token).append(expressionToken);
                } else {
                    String expression = token + expressionToken + tokens.nextToken();
                    ejbqlBuilder.append(':').append(getParameterName(startingParameterNumber + parameterValueBindings.size()));
                    parameterValueBindings.add(expressions.createValueExpression(expression));
                }
            } else {
                ejbqlBuilder.append(token);
            }
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<ValueExpression> getParameterValueBindings()
    {
        return parameterValueBindings;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getEjbql()
    {
        return ejbqlBuilder.toString();
    }
}
