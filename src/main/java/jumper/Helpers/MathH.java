package jumper.Helpers;

public class MathH
{
    public static double round(double num, double precision)
    {
        var scale = Math.pow(10, precision);
        return Math.round(num * scale) / scale;
    }
}
