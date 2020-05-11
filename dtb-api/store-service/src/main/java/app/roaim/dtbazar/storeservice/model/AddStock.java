package app.roaim.dtbazar.storeservice.model;

import lombok.Value;

// TODO move to a separate micro service
@Value
public class AddStock {
    double quantity;
    double unitPrice;
}
