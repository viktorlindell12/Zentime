CREATE INDEX idx_bookings_service_id  ON bookings (service_id);
CREATE INDEX idx_bookings_customer_id ON bookings (customer_id);
CREATE INDEX idx_bookings_start_time  ON bookings (start_time);
CREATE INDEX idx_services_business_id ON services (business_id);