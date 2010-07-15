#inventory-rpc-service.thrift
namespace java com.focaplo.supverinventory.rpc

struct InventoryLookUp {
  1: i32 uid,
  2: string itemNumber
}

struct InventoryLookUpResponse {
  1: i32 uid,
  2: string itemNumber,
  3: i32 quantityOnHand
}

struct QuantityRefresh{
  1: i32 uid,
  2: string itemNumber,
  3: i32 currentOnHandQuantity,
  4: string warehouseNumber
}

struct QuantityRefreshResponse{
  1: i32 uid,
  2: string responseCode
}

struct QuantityUpdate{
  1: i32 uid,
  2: string itemNumber,
  3: i32 quantityChange,
  4: string warehouseNumber,
  5: string reason
}

struct QuantityUpdateResponse{
  1: i32 uid,
  2: string responseCode
}

struct itemReservation{
  1: i32 uid,
  2: string itemNumber,
  3: i32 reserveQuantity,
  4: string orderNumber,
  5: string fromId
}

struct itemReservationResponse{
  1: i32 uid,
  2: string responseCode,
  3: string reservationNumberOrOthers
}

service InventoryRpcServer{
     InventoryLookUpResponse lookUp(1:InventoryLookUp request),
     itemReservationResponse reserve(1:itemReservation request),
     QuantityUpdateResponse updateQuantity(1:QuantityUpdate request),
     QuantityRefreshResponse refreshQuantity(1:QuantityRefresh request)
}