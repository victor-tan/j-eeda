package models.yh.arap;import java.util.List;import com.jfinal.plugin.activerecord.Model;public class ArapMiscCostOrderDTO  {	private ArapMiscCostOrder order;		private List<ArapMiscCostOrderItem> itemList;	public List<ArapMiscCostOrderItem> getItemList() {		return itemList;	}	public void setItemList(List<ArapMiscCostOrderItem> itemList) {		this.itemList = itemList;	}	public ArapMiscCostOrder getOrder() {		return order;	}	public void setOrder(ArapMiscCostOrder order) {		this.order = order;	}	}