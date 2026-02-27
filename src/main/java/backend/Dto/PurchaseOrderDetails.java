package backend.Dto;

import backend.Entities.PurchaseOrder;
import backend.Entities.PurchaseOrderDetail;

import java.util.List;

public record PurchaseOrderDetails(
        PurchaseOrder purchaseOrder,
        List<PurchaseOrderDetail> purchaseOrderDetail
) {
}
