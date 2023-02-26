export const sum = (arr) => {
    let result = 0;
    for(const elem of arr) {
      result += elem;
    }
    return result;
}

export const calculateStatus = (blockedFunds, totalFunds) => {
    let status;
    const divider = blockedFunds + totalFunds;
    const fundsProportion = totalFunds / divider;
    if(fundsProportion < 0.5) {
        status = fundsProportion * 50;
    } else {
        status = fundsProportion * 100;
    }
    return status;
}