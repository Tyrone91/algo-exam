
public class TimeMeasurement {
    
    private long m_StartTime;
    private long m_EndTime;
    private String m_Name;
    
    private TimeMeasurement() {
        
    }
    
    public String name() {
        return m_Name;
    }
    
    public long time() {
        return this.endtime() - this.starttime();
    }
    
    public long starttime() {
        return m_StartTime;
    }
    
    public long endtime() {
        return m_EndTime;
    }
    
    public TimeMeasurement stop() {
        m_EndTime = System.currentTimeMillis();
        return this;
    }
    
    public void print() {
        System.out.println(this.toString());
    }
    
    @Override
    public String toString() {
        return String.format("timer '%s' run %s ms or %s s. From %s to %s", this.name(), this.time(), this.time() / 1000f, this.starttime(), this.endtime());
    }
    
    public static TimeMeasurement now() {
        return TimeMeasurement.now("NAMELESS");
    }
    
    public static TimeMeasurement now(String name) {
        TimeMeasurement res = new TimeMeasurement();
        res.m_Name = name;
        res.m_StartTime = System.currentTimeMillis();
        return res;
    }
}
