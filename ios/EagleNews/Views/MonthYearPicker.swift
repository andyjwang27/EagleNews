import SwiftUI

struct MonthYearPicker: View {
    @Binding var displayedMonth: Date

    @State private var selectedMonth: Int
    @State private var selectedYear: Int

    private let calendar = Calendar.current

    private var years: [Int] {
        let currentYear = calendar.component(
            .year,
            from: Date()
        )

        return Array(
            (currentYear - 5)...(currentYear + 5)
        )
    }

    init(displayedMonth: Binding<Date>) {
        self._displayedMonth = displayedMonth

        let date = displayedMonth.wrappedValue

        _selectedMonth = State(
            initialValue: Calendar.current.component(
                .month,
                from: date
            )
        )

        _selectedYear = State(
            initialValue: Calendar.current.component(
                .year,
                from: date
            )
        )
    }

    var body: some View {
        HStack(spacing: 0) {
            Picker("Month", selection: $selectedMonth) {
                ForEach(1...12, id: \.self) { month in
                    Text(calendar.monthSymbols[month - 1])
                        .tag(month)
                }
            }
            .pickerStyle(.wheel)

            Picker("Year", selection: $selectedYear) {
                ForEach(years, id: \.self) { year in
                    Text("\(year)")
                        .tag(year)
                }
            }
            .pickerStyle(.wheel)
            .environment(\.locale, Locale(identifier: "en_US_POSIX"))
        }
        .onChange(of: selectedMonth) {
            updateDisplayedMonth()
        }
        .onChange(of: selectedYear) {
            updateDisplayedMonth()
        }
        .frame(width: 250, height: 125)
        .background(.ultraThinMaterial)
    }

    private func updateDisplayedMonth() {
        if let newDate = calendar.date(
            from: DateComponents(
                year: selectedYear,
                month: selectedMonth,
                day: 1
            )
        ) {
            withAnimation {
                displayedMonth = newDate
            }
        }
    }
}
