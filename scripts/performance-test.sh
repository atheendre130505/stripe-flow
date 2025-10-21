#!/bin/bash

# StripeFlow Performance Testing Script
# Target: 1000+ transactions per second with <100ms response times

set -e

# Configuration
API_BASE_URL="http://localhost:8080"
API_KEY="test-api-key"
TARGET_TPS=1000
TARGET_RESPONSE_TIME=100
TEST_DURATION=60
CONCURRENT_USERS=100

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 StripeFlow Performance Testing${NC}"
echo "=================================="
echo "Target TPS: $TARGET_TPS"
echo "Target Response Time: ${TARGET_RESPONSE_TIME}ms"
echo "Test Duration: ${TEST_DURATION}s"
echo "Concurrent Users: $CONCURRENT_USERS"
echo ""

# Check if required tools are installed
check_dependencies() {
    echo -e "${YELLOW}📋 Checking dependencies...${NC}"
    
    if ! command -v curl &> /dev/null; then
        echo -e "${RED}❌ curl is required but not installed${NC}"
        exit 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo -e "${RED}❌ jq is required but not installed${NC}"
        exit 1
    fi
    
    if ! command -v ab &> /dev/null; then
        echo -e "${YELLOW}⚠️  Apache Bench (ab) not found, using curl for testing${NC}"
    fi
    
    echo -e "${GREEN}✅ Dependencies check passed${NC}"
}

# Test API connectivity
test_connectivity() {
    echo -e "${YELLOW}🔗 Testing API connectivity...${NC}"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" \
        -H "X-API-Key: $API_KEY" \
        -H "Content-Type: application/json" \
        "$API_BASE_URL/api/v1/performance/health")
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ API is accessible${NC}"
    else
        echo -e "${RED}❌ API is not accessible (HTTP $response)${NC}"
        exit 1
    fi
}

# Run load test using Apache Bench
run_ab_test() {
    echo -e "${YELLOW}📊 Running Apache Bench load test...${NC}"
    
    # Create test data
    cat > /tmp/test_data.json << EOF
{
    "amount": 1000,
    "currency": "USD",
    "customerId": 1,
    "description": "Performance test charge"
}
EOF
    
    # Run AB test
    ab -n $((TARGET_TPS * TEST_DURATION)) \
       -c $CONCURRENT_USERS \
       -H "X-API-Key: $API_KEY" \
       -H "Content-Type: application/json" \
       -p /tmp/test_data.json \
       -T "application/json" \
       "$API_BASE_URL/api/v1/charges" > /tmp/ab_results.txt 2>&1
    
    # Parse results
    parse_ab_results
}

# Parse Apache Bench results
parse_ab_results() {
    echo -e "${YELLOW}📈 Parsing test results...${NC}"
    
    # Extract key metrics
    total_requests=$(grep "Complete requests:" /tmp/ab_results.txt | awk '{print $3}')
    failed_requests=$(grep "Failed requests:" /tmp/ab_results.txt | awk '{print $3}')
    requests_per_second=$(grep "Requests per second:" /tmp/ab_results.txt | awk '{print $4}')
    time_per_request=$(grep "Time per request:" /tmp/ab_results.txt | head -1 | awk '{print $4}')
    mean_time=$(grep "Mean time per request:" /tmp/ab_results.txt | awk '{print $4}')
    
    echo ""
    echo -e "${BLUE}📊 Test Results:${NC}"
    echo "=================="
    echo "Total Requests: $total_requests"
    echo "Failed Requests: $failed_requests"
    echo "Requests per Second: $requests_per_second"
    echo "Time per Request: ${time_per_request}ms"
    echo "Mean Time: ${mean_time}ms"
    echo ""
    
    # Check if targets are met
    check_performance_targets "$requests_per_second" "$mean_time"
}

# Run load test using curl
run_curl_test() {
    echo -e "${YELLOW}📊 Running curl-based load test...${NC}"
    
    local start_time=$(date +%s)
    local end_time=$((start_time + TEST_DURATION))
    local request_count=0
    local success_count=0
    local failure_count=0
    local total_response_time=0
    
    echo "Starting load test for ${TEST_DURATION} seconds..."
    
    while [ $(date +%s) -lt $end_time ]; do
        # Create test data
        local test_data='{"amount":1000,"currency":"USD","customerId":1,"description":"Performance test"}'
        
        # Measure response time
        local response_start=$(date +%s%3N)
        
        # Make request
        local response=$(curl -s -w "%{http_code}" \
            -H "X-API-Key: $API_KEY" \
            -H "Content-Type: application/json" \
            -d "$test_data" \
            "$API_BASE_URL/api/v1/charges" 2>/dev/null)
        
        local response_end=$(date +%s%3N)
        local response_time=$((response_end - response_start))
        
        request_count=$((request_count + 1))
        total_response_time=$((total_response_time + response_time))
        
        # Check response
        if [[ "$response" =~ ^[0-9]+$ ]] && [ "$response" -ge 200 ] && [ "$response" -lt 300 ]; then
            success_count=$((success_count + 1))
        else
            failure_count=$((failure_count + 1))
        fi
        
        # Rate limiting
        sleep 0.001  # 1ms delay to prevent overwhelming the server
    done
    
    local actual_duration=$((end_time - start_time))
    local tps=$((request_count / actual_duration))
    local avg_response_time=$((total_response_time / request_count))
    
    echo ""
    echo -e "${BLUE}📊 Test Results:${NC}"
    echo "=================="
    echo "Total Requests: $request_count"
    echo "Successful Requests: $success_count"
    echo "Failed Requests: $failure_count"
    echo "Requests per Second: $tps"
    echo "Average Response Time: ${avg_response_time}ms"
    echo "Test Duration: ${actual_duration}s"
    echo ""
    
    # Check if targets are met
    check_performance_targets "$tps" "$avg_response_time"
}

# Check performance targets
check_performance_targets() {
    local tps=$1
    local response_time=$2
    
    echo -e "${YELLOW}🎯 Checking performance targets...${NC}"
    
    # Check TPS target
    if [ "$tps" -ge "$TARGET_TPS" ]; then
        echo -e "${GREEN}✅ TPS target met: $tps >= $TARGET_TPS${NC}"
    else
        echo -e "${RED}❌ TPS target not met: $tps < $TARGET_TPS${NC}"
    fi
    
    # Check response time target
    if [ "$response_time" -le "$TARGET_RESPONSE_TIME" ]; then
        echo -e "${GREEN}✅ Response time target met: ${response_time}ms <= ${TARGET_RESPONSE_TIME}ms${NC}"
    else
        echo -e "${RED}❌ Response time target not met: ${response_time}ms > ${TARGET_RESPONSE_TIME}ms${NC}"
    fi
    
    # Overall assessment
    if [ "$tps" -ge "$TARGET_TPS" ] && [ "$response_time" -le "$TARGET_RESPONSE_TIME" ]; then
        echo -e "${GREEN}🎉 All performance targets achieved!${NC}"
        exit 0
    else
        echo -e "${RED}⚠️  Performance targets not fully achieved${NC}"
        exit 1
    fi
}

# Run cache performance test
run_cache_test() {
    echo -e "${YELLOW}🗄️  Running cache performance test...${NC}"
    
    local response=$(curl -s -H "X-API-Key: $API_KEY" \
        "$API_BASE_URL/api/v1/performance/cache-test?iterations=1000")
    
    echo "Cache Test Results:"
    echo "$response" | jq '.'
}

# Run database performance test
run_database_test() {
    echo -e "${YELLOW}🗄️  Running database performance test...${NC}"
    
    local response=$(curl -s -H "X-API-Key: $API_KEY" \
        "$API_BASE_URL/api/v1/performance/database-test?iterations=100")
    
    echo "Database Test Results:"
    echo "$response" | jq '.'
}

# Get system metrics
get_system_metrics() {
    echo -e "${YELLOW}📊 Getting system metrics...${NC}"
    
    local response=$(curl -s -H "X-API-Key: $API_KEY" \
        "$API_BASE_URL/api/v1/performance/metrics")
    
    echo "System Metrics:"
    echo "$response" | jq '.'
}

# Main execution
main() {
    echo -e "${BLUE}🚀 Starting StripeFlow Performance Testing${NC}"
    echo "=============================================="
    
    # Check dependencies
    check_dependencies
    
    # Test connectivity
    test_connectivity
    
    # Run performance tests
    if command -v ab &> /dev/null; then
        run_ab_test
    else
        run_curl_test
    fi
    
    # Run additional tests
    echo ""
    echo -e "${YELLOW}🔍 Running additional performance tests...${NC}"
    
    run_cache_test
    echo ""
    
    run_database_test
    echo ""
    
    get_system_metrics
    echo ""
    
    echo -e "${GREEN}✅ Performance testing completed!${NC}"
}

# Run main function
main "$@"
