package us.entwine;

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget.RelativeLayout
import android.view.LayoutInflater
import android.widget.TextView
import us.entwine.models.Event
import android.util.AttributeSet
import android.content.Context
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.View
import us.entwine.utility.HttpHelpers
import us.entwine.utility.HttpApis
import scala.concurrent.ExecutionContext.Implicits.global

class ActivityDashboard extends Activity {
    private val self = this
    
    class EventListItemView (context: Context, attrs: AttributeSet, event: Event) extends RelativeLayout(context, attrs) {
        def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)
        def this(context: Context) = this(context, null, null)
        
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
        inflater.inflate(R.layout.listitem_event, this, true)
        
        findViewById(R.id.eventName).asInstanceOf[TextView].setText(event.name)
    }
    
    class EventListAdapter(eventList: List[Event]) extends BaseAdapter {
        val eventItemList = eventList.map { item =>
            new EventListItemView(self, null, item)
        }
        
        override def getCount() = { eventList.length }
        override def getItem(position: Int) = eventList(position)
        override def getItemId(position: Int) = position
        override def getView(position: Int, convertView: View, parent: ViewGroup): View = eventItemList(position)
    }

    protected override def onCreate (savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        HttpApis.getEvents() map { events =>
            findViewById(R.id.eventList).asInstanceOf[ListView].setAdapter(new EventListAdapter(events))
        }
    }

}
